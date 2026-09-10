# FinGuard AI

FinGuard AI e uma plataforma backend de portfolio para deteccao de fraude, investigacao assistida por IA e inteligencia de disputas financeiras.

O projeto nasce como um monorepo Maven em Java 25, com servicos Spring Boot independentes e comunicacao orientada a eventos. A IA entra como camada de investigacao e recomendacao, sempre auditavel e sem permissao direta para mover dinheiro, aprovar chargebacks, bloquear contas ou executar reembolsos.

## Estado atual

Fase 10 concluída: os serviços expõem métricas Prometheus e correlação HTTP para operação local.

## Modulos

- `libs/event-contracts`: contratos de eventos compartilhados.
- `libs/common-observability`: base para correlacao e observabilidade compartilhada.
- `services/payment-service`: entrada e consulta de transacoes.
- `services/fraud-detection-service`: regras deterministicas de fraude.
- `services/fraud-ai-service`: investigacao assistida por IA.
- `services/dispute-service`: ciclo de vida de disputas.
- `services/dispute-agent-service`: agentes de disputa e coleta de evidencias.
- `services/decision-service`: politicas deterministicas de decisao.
- `services/knowledge-service`: base de conhecimento e RAG.
- `services/notification-service`: notificacoes operacionais.

## O que cada servico faz

`payment-service` recebe transacoes, valida os dados de entrada, persiste o registro financeiro e grava o evento `TransactionCreated` na outbox. Ele e o ponto inicial do fluxo e nao decide fraude sozinho.

`fraud-detection-service` consome `TransactionCreated`, aplica regras deterministicas de fraude e calcula score de risco. Quando a transacao fica em risco medio ou alto, registra um caso de fraude e publica `FraudSuspected`.

`fraud-ai-service` consome eventos `fraud.suspected`, produz recomendações estruturadas para revisão humana ou coleta de contexto e audita cada recomendação no próprio banco. O stub inicial é determinístico e não executa decisões financeiras nem usa provedores externos.

`dispute-service` cuidara do ciclo de vida das disputas, incluindo abertura, analise, evidencias, prazos, estados e revisao humana.

`dispute-agent-service` orquestrara agentes voltados a disputas, como coleta de evidencias, organizacao de contexto e apoio a analistas. Ele trabalha sobre dados auditaveis e nao aprova chargebacks diretamente.

`decision-service` concentrara politicas deterministicas de decisao. Ele transforma sinais de fraude, IA, disputas e regras de negocio em recomendacoes ou decisoes permitidas pelo fluxo.

`knowledge-service` mantera a base de conhecimento usada por RAG, com documentos, politicas, embeddings e citacoes. Ele ajuda os servicos de IA a responder com contexto rastreavel.

`notification-service` enviara comunicacoes operacionais, como alertas de fraude, eventos de disputa e notificacoes internas para revisao humana.

## Disputas

O `dispute-service` abre disputas para transações, registra evidências e controla o ciclo de vida `OPEN`, `EVIDENCE_REQUESTED`, `UNDER_REVIEW`, `RESOLVED` e `REJECTED`. A revisão exige um identificador humano e uma decisão explícita; o serviço não aprova ou executa chargebacks diretamente.

Endpoints principais:

- `POST /disputes`: abre uma disputa;
- `GET /disputes/{id}`: consulta a disputa e suas evidências;
- `POST /disputes/{id}/evidence-request`: solicita evidências;
- `POST /disputes/{id}/evidence`: anexa uma evidência;
- `POST /disputes/{id}/review`: registra a revisão humana.

## Agentes de disputa

O `dispute-agent-service` usa LangChain4j para declarar ferramentas de consulta somente leitura. O orquestrador determinístico pode solicitar contexto, solicitar evidências ou encaminhar uma disputa com evidências para revisão manual. O agente não altera estados, cria evidências, aprova disputas nem executa chargebacks.

## Decisões determinísticas

O `decision-service` recebe score e nível de fraude, recomendação da IA e status de disputa em `POST /decisions`. Scores altos, risco `HIGH` ou recomendação `REVIEW_MANUALLY` produzem `HOLD_FOR_REVIEW`; sem sinais de escalonamento, o resultado é `APPROVE`. Toda decisão e seus sinais são persistidos para auditoria.

## Observabilidade

Todos os serviços expõem `/actuator/prometheus` e preservam ou geram `X-Correlation-Id`. O Compose local inclui Prometheus em `http://localhost:9090` e Grafana em `http://localhost:3000`. O runbook está em `docs/observability.md`.

## Resiliencia de eventos

O `payment-service` usa outbox transacional para evitar perda de eventos: a transacao e o evento `TransactionCreated` sao gravados na mesma transacao de banco. Um publisher agendado tenta publicar eventos pendentes no Kafka, registra tentativas, guarda o ultimo erro e marca o evento como `FAILED` quando excede o limite configurado.

O `fraud-detection-service` usa uma tabela de transacoes processadas para evitar reprocessamento do mesmo `transactionId`. Falhas no consumo de `transaction.created` passam por retry com backoff fixo e, se continuarem falhando, sao enviadas para `transaction.created.dlt`.

Metricas Micrometer acompanham publicacoes da outbox, falhas da outbox, mensagens consumidas, mensagens publicadas e mensagens enviadas para DLT.

## Velocidade de transacoes

O `fraud-detection-service` usa Kafka Streams para agregar `transaction.created` em janelas de 10 minutos por cliente. Cada atualizacao gera `FraudVelocityUpdated` em `fraud.velocity.updated`.

O proprio servico consome esse evento, persiste o snapshot em `velocity_snapshots` e usa o sinal `CUSTOMER_VELOCITY` no motor de regras quando o volume recente do cliente ultrapassa o limite configurado no codigo da fase.

Sinais de recusas em janela curta ainda dependem de eventos de transacao recusada, que serao adicionados quando o fluxo de autorizacao ganhar estados de recusa.

## Investigacao assistida por IA

O `fraud-ai-service` consome `fraud.suspected` e classifica a recomendação conforme o nível de risco: `REVIEW_MANUALLY` para risco alto, `COLLECT_MORE_CONTEXT` para risco médio e `NO_ACTION` para risco baixo. As evidências vêm dos sinais determinísticos do evento, são somente leitura e cada resultado é persistido em `fraud_investigation_audits`.

## Base de conhecimento e RAG

O `knowledge-service` recebe documentos em `POST /knowledge/documents` e realiza busca determinística em `GET /knowledge/search`. Cada resultado inclui trecho encontrado e citação com o documento e sua fonte. O schema próprio usa PostgreSQL com extensão pgvector; a coluna de embedding fica preparada para a próxima evolução sem depender de provedor externo nesta fase.

## Arquitetura

```mermaid
flowchart LR
    Client[Cliente/API Consumer] --> Payment[payment-service]

    Payment --> PaymentDb[(finguard_payment)]
    Payment --> Outbox[(outbox_events)]
    Outbox -->|transaction.created| Kafka[(Kafka)]

    Kafka -->|transaction.created| FraudDetection[fraud-detection-service]
    FraudDetection -->|fraud.suspected| Kafka
    FraudDetection -->|fraud.velocity.updated| Kafka
    Kafka --> FraudAi[fraud-ai-service]
    Kafka --> Dispute[dispute-service]
    Kafka --> Decision[decision-service]
    Kafka --> Notification[notification-service]

    FraudDetection --> FraudDb[(finguard_fraud_detection)]
    FraudAi --> FraudAiDb[(finguard_fraud_ai)]
    Dispute --> DisputeDb[(finguard_dispute)]
    Decision --> DecisionDb[(finguard_decision)]
    Knowledge[knowledge-service] --> KnowledgeDb[(finguard_knowledge)]
    Notification --> NotificationDb[(finguard_notification)]
    DisputeAgent[dispute-agent-service] --> DisputeAgentDb[(finguard_dispute_agent)]

    FraudAi --> Knowledge
    DisputeAgent --> Knowledge
    DisputeAgent --> Dispute

    Redis[(Redis)] --> FraudDetection
    Redis --> FraudAi

    classDef service fill:#eef6ff,stroke:#2563eb,color:#111827
    classDef datastore fill:#f8fafc,stroke:#64748b,color:#111827
    classDef eventbus fill:#fff7ed,stroke:#ea580c,color:#111827

    class Payment,FraudDetection,FraudAi,Dispute,DisputeAgent,Decision,Knowledge,Notification service
    class PaymentDb,Outbox,FraudDb,FraudAiDb,DisputeDb,DisputeAgentDb,DecisionDb,KnowledgeDb,NotificationDb,Redis datastore
    class Kafka eventbus
```

## Requisitos locais

- Java 25
- Maven 3.6.3 ou superior
- Docker
- Docker Compose

## Como validar

```bash
./mvnw clean verify
docker compose config
docker compose up -d
```

Depois que os servicos forem iniciados localmente, cada aplicacao expoe:

```text
GET /actuator/health
```

Portas locais planejadas:

- `payment-service`: `8081`
- `fraud-detection-service`: `8082`
- `fraud-ai-service`: `8083`
- `dispute-service`: `8084`
- `dispute-agent-service`: `8085`
- `decision-service`: `8086`
- `knowledge-service`: `8087`
- `notification-service`: `8088`

## Infraestrutura local

O `docker-compose.yml` sobe:

- PostgreSQL 17 com pgvector na porta `5432`
- Kafka na porta `29092`
- Redis na porta `6379`

O PostgreSQL cria bancos separados por servico. Esta separacao protege a regra de ownership: um servico nao consulta tabelas internas de outro servico.

## Decisoes arquiteturais

As decisoes iniciais estao em `docs/adr`:

- ADR 0001: monorepo Maven
- ADR 0002: arquitetura orientada a eventos
- ADR 0003: IA sem autoridade transacional
- ADR 0004: ownership de dados por servico
- ADR 0005: resiliencia de eventos
- ADR 0006: sinais de velocidade com Kafka Streams
- ADR 0007: agentes de disputa somente leitura (histórico)
- ADR 0008: agentes de disputa somente leitura (oficial da Fase 8)

# Progresso do FinGuard AI

Este arquivo registra o que ja foi implementado, o que ainda falta e qual e o melhor ponto para retomar o trabalho.

## Como usar este documento

Ao final de cada tarefa, atualize:

- a fase atual;
- os PRs e commits relevantes;
- as validacoes executadas;
- o proximo passo recomendado;
- qualquer pendencia tecnica ou decisao aberta.

## Estado atual

Fase atual: Fase 9 implementada em branch.

Branch atual:

```text
fase-9-decision-service
```

PR atual:

```text
PR ainda nao criado.
```

## Implementado

### Fase 0: base do monorepo

Status: concluida e mesclada na `main`.

Entregas:

- monorepo Maven com Java 25;
- oito servicos Spring Boot minimos;
- bibliotecas `event-contracts` e `common-observability`;
- `docker-compose.yml` com PostgreSQL, Kafka e Redis;
- bancos separados por servico no PostgreSQL;
- health check via Actuator;
- README inicial em portugues;
- ADRs 0001 a 0004.

### Fase 1: fluxo inicial de transacoes

Status: concluida e mesclada na `main`.

Entregas:

- `payment-service` com agregado `Transaction`;
- endpoint `POST /transactions`;
- endpoint `GET /transactions/{id}`;
- validacao de entrada;
- tratamento de erro com `ProblemDetail`;
- persistencia com JPA e Flyway;
- tabela `transactions`;
- tabela `outbox_events`;
- contrato `TransactionCreatedEvent`;
- gravação da outbox na mesma transacao do banco;
- publisher Kafka agendado para `transaction.created`;
- testes de dominio e integracao com Testcontainers.

PR:

```text
https://github.com/Cosmess/finguard-ai/pull/1
```

### Fase 2: deteccao deterministica de fraude

Status: concluida e mesclada na `main`.

Entregas:

- `fraud-detection-service` com JPA, Flyway, PostgreSQL e Kafka;
- contrato `FraudSuspectedEvent`;
- `TransactionCreatedEvent` enriquecido com `deviceId` e `ipAddress`;
- regras deterministicas `HIGH_VALUE`, `NEW_DEVICE` e `UNUSUAL_HOUR`;
- score e classificacao `LOW`, `MEDIUM` e `HIGH`;
- tabela `fraud_cases`;
- tabela `processed_transactions`;
- consumidor de `transaction.created`;
- publicador de `fraud.suspected`;
- protecao contra duplicidade por `transactionId`;
- README atualizado com diagrama Mermaid e responsabilidades dos servicos;
- testes de regras, aplicacao e contexto com Testcontainers.

PR:

```text
https://github.com/Cosmess/finguard-ai/pull/2
```

### Fase 3: resiliencia de eventos

Status: concluida e mesclada na `main`.

Entregas:

- retry na outbox do `payment-service`;
- campos `attempts`, `last_error` e status `FAILED` para eventos da outbox;
- limite configuravel `payment.outbox.max-attempts`;
- metricas Micrometer para eventos publicados e falhas de outbox;
- `CommonErrorHandler` no `fraud-detection-service`;
- retry com backoff fixo para consumo Kafka;
- envio de falhas persistentes para `transaction.created.dlt`;
- metricas para mensagens consumidas, publicadas e enviadas para DLT;
- ADR 0005 sobre resiliencia de eventos;
- README atualizado com a Fase 3.

Commits:

```text
5f0bab0 feat(payment): adiciona retry na outbox
7425942 feat(fraude): configura retry e dlt no Kafka
8932517 docs(arquitetura): documenta resiliencia de eventos
396f3b9 docs(progresso): registra estado das fases
```

PR:

```text
https://github.com/Cosmess/finguard-ai/pull/3
```

### Fase 4: Kafka Streams e velocidade

Status: concluida e mesclada na `main`.

Entregas:

- dependencia `kafka-streams` no `fraud-detection-service`;
- topologia Kafka Streams para agregar `transaction.created` por cliente;
- janela configurada por `fraud.detection.velocity-window`;
- contrato `FraudVelocityUpdatedEvent`;
- publicacao de `fraud.velocity.updated`;
- tabela `velocity_snapshots`;
- consumo de snapshots de velocidade;
- regra `CUSTOMER_VELOCITY` integrada ao score de fraude;
- testes de SerDe, regras e atualizacao de snapshots.

Commits:

```text
6797e4c feat(fraude): adiciona topologia de velocidade
4b58334 feat(fraude): incorpora sinais de velocidade ao score
```

## Validacoes recentes

Executadas na Fase 9:

```text
./mvnw clean -pl services/decision-service -am test -> BUILD SUCCESS (3 testes)
./mvnw clean verify -> BUILD SUCCESS
```

## Falta implementar

### Fase 5: servico de IA para fraude

Status: concluida e mesclada na `main`.

Entregas:

- `fraud-ai-service` consome `fraud.suspected`;
- stub deterministico para niveis `HIGH`, `MEDIUM` e `LOW`;
- recomendacoes estruturadas e validadas;
- recomendacao de contexto adicional explicitamente somente leitura;
- auditoria persistida em `fraud_investigation_audits`;
- nenhuma chave ou cliente de provedor externo em CI.

Branch:

```text
fase-5-fraud-ai-service
```

### Fase 6: knowledge-service e RAG

Status: concluida e mesclada na `main`.

Entregas:

- persistencia de documentos no `knowledge-service`;
- endpoint de ingestao `POST /knowledge/documents`;
- busca deterministica em `GET /knowledge/search`;
- resultados com trechos e citacoes de fonte;
- schema PostgreSQL com extensao pgvector e coluna de embedding preparada;
- testes de contexto e busca deterministica.

Branch:

```text
fase-6-knowledge-service-rag
```

### Fase 7: disputas

Status: concluida e mesclada na `main`.

Entregas:

- agregado de disputa com persistencia própria;
- estados `OPEN`, `EVIDENCE_REQUESTED`, `UNDER_REVIEW`, `RESOLVED` e `REJECTED`;
- registro de evidências com referência auditável;
- revisão humana explícita com decisão `APPROVE` ou `REJECT`;
- endpoints REST para abertura, consulta, evidências e revisão;
- testes determinísticos de ciclo de vida e transições inválidas.

Branch:

```text
fase-7-disputes
```

### Fase 8: agentes com LangChain4j

Status: concluida e mesclada na `main`.

Entregas:

- dependência LangChain4j no `dispute-agent-service`;
- ferramentas `@Tool` somente leitura para status e evidências;
- orquestração determinística explícita;
- recomendações `REQUEST_CONTEXT`, `REQUEST_EVIDENCE` e `REVIEW_MANUALLY`;
- nenhum modelo externo ou credencial em CI;
- ADRs 0007 e 0008 sobre agentes sem autoridade transacional;
- testes de orquestração e limites de permissão.

Branch:

```text
fase-8-agents-langchain4j
```

### Fase 9: decision-service

Status: implementada em branch, aguardando abertura de PR.

Entregas:

- política determinística com threshold de fraude;
- integração de sinais de fraude, recomendação de IA e status de disputa;
- resultados `APPROVE` e `HOLD_FOR_REVIEW`;
- persistência das decisões e sinais para auditoria;
- endpoint `POST /decisions`;
- testes de baixo risco, alto risco e contexto Spring.

Branch:

```text
fase-9-decision-service
```

### Fase 10: observabilidade

- OpenTelemetry;
- Prometheus;
- Grafana;
- dashboards e runbooks.

### Fase 11: seguranca

- JWT;
- RBAC;
- configuracao Spring Security.

### Fase 12: frontend opcional

- interface Next.js somente depois do backend estabilizar.

## Proximo ponto de retomada

1. Criar o PR da Fase 9 para `main`.
2. Mesclar o PR quando estiver aprovado.
3. Criar a branch da Fase 10 para observabilidade.
4. Atualizar este arquivo ao final da tarefa.

## Pendencias e observacoes

- O projeto usa `pgvector/pgvector:pg17`, porque o Flyway disponivel no Spring Boot 4.1.1 nao aceitou PostgreSQL 18 durante os testes.
- Nao foi executado `docker compose down --volumes`, pois isso apagaria dados locais do PostgreSQL.
- O README e a documentacao podem ficar em portugues; nomes de classes, metodos, APIs e eventos seguem em ingles.

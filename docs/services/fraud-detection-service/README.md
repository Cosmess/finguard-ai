# fraud-detection-service

## Responsabilidade

Consome transacoes criadas, aplica regras deterministicas de fraude, calcula score e publica `FraudSuspected` quando o risco e medio ou alto. Tambem calcula sinais de velocidade por cliente com Kafka Streams.

## Arquitetura

- `domain`: regras `HIGH_VALUE`, `NEW_DEVICE`, `UNUSUAL_HOUR` e `CUSTOMER_VELOCITY`;
- `application`: avaliacao, idempotencia, snapshots e acesso a sinais;
- `adapter/in/kafka`: consumidores de transacoes e velocidade;
- `adapter/streams`: topologia Kafka Streams e SerDe;
- `adapter/out/kafka`: publicacao de eventos;
- `adapter/out/persistence`: casos, transacoes processadas e snapshots;
- Flyway: schema do banco de fraude.

O score e calculado sem autoridade de pagamento: o servico apenas registra sinais e suspeitas.

## Integracoes

- Entrada Kafka: `transaction.created`.
- Saida Kafka: `fraud.suspected` e `fraud.velocity.updated`.
- Consumidores downstream: `fraud-ai-service`, `decision-service`, `notification-service` e futuros fluxos de disputa.
- Banco: `finguard_fraud_detection`.
- Estado de stream: janela de 10 minutos por cliente.
- Dependencias locais: Kafka, PostgreSQL e Redis conforme o ambiente.

## Resiliencia e idempotencia

A tabela `processed_transactions` evita reprocessar o mesmo `transactionId`. O consumidor usa retry com backoff e envia falhas persistentes para `transaction.created.dlt`.

## Observabilidade

Expone `/actuator/health` e `/actuator/prometheus`. As metricas acompanham mensagens consumidas, publicadas, enviadas para DLT e atualizacoes de velocidade.

## Execucao e testes

```bash
./mvnw -pl services/fraud-detection-service -am test
```

Os testes cobrem regras, aplicacao, SerDe, snapshots e contexto Spring.

## Limites

Sinais de recusas ainda dependem de eventos de autorizacao recusada, que nao existem no fluxo de pagamentos atual.

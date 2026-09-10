# ADR 0006: Sinais de velocidade com Kafka Streams

## Status

Aceita

## Contexto

Fraudes frequentemente aparecem como volume anormal de transacoes em uma janela curta. Esses sinais precisam ser calculados continuamente e usados pelo motor deterministico sem exigir consultas caras em tempo real.

## Decisao

Usaremos Kafka Streams no `fraud-detection-service` para agregar eventos `transaction.created` por janela de tempo. A primeira dimensao implementada e cliente, com janela de 10 minutos.

Cada atualizacao publica `FraudVelocityUpdated` em `fraud.velocity.updated`. O servico consome esse evento, persiste o snapshot mais recente por dimensao e usa `CUSTOMER_VELOCITY` como regra adicional no score de fraude.

## Consequencias

- O motor de fraude recebe sinais de comportamento recente sem consultar historico bruto a cada transacao.
- Snapshots persistidos deixam o sinal auditavel e reutilizavel.
- Novas dimensoes, como dispositivo, cartao ou recusas, podem ser adicionadas como novas agregacoes.
- Sinais de recusas dependem de eventos de recusa ainda nao existentes no fluxo de pagamentos.

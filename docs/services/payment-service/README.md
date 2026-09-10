# payment-service

## Responsabilidade

Recebe transacoes financeiras, valida a entrada, persiste o agregado `Transaction` e publica `TransactionCreated` por meio de uma outbox transacional. O servico e a porta de entrada do fluxo financeiro e nao decide fraude.

## Arquitetura

O servico segue uma separacao hexagonal simples:

- `domain`: agregado `Transaction`, status e regras de dominio;
- `application`: casos de uso de criacao e consulta;
- `adapter/in/web`: endpoints REST;
- `adapter/out/persistence`: JPA e Flyway;
- `adapter/out/outbox`: persistencia e publicacao Kafka da outbox.

A transacao financeira e o registro da outbox sao gravados no mesmo commit do PostgreSQL.

## Integracoes

- Entrada: cliente HTTP em `POST /transactions` e `GET /transactions/{id}`.
- Saida: evento `transaction.created` para o Kafka.
- Consumo indireto: `fraud-detection-service` inicia a avaliacao depois do evento.
- Banco: `finguard_payment`.
- Dependencias locais: PostgreSQL e Kafka.

## Resiliencia

O publisher agendado tenta eventos `PENDING`, incrementa tentativas, guarda o ultimo erro e marca `FAILED` depois de `payment.outbox.max-attempts`. O evento nunca depende de uma chamada Kafka dentro da transacao financeira.

## Seguranca

Este e o servico financeiro protegido por JWT. O segredo vem de `FINGUARD_JWT_SECRET`. Health e info sao publicos; metricas exigem `ROLE_OPS`; os demais endpoints exigem token valido.

## Observabilidade

Expone `/actuator/health` e `/actuator/prometheus`, preserva ou gera `X-Correlation-Id` e publica metricas de outbox.

## Execucao e testes

```bash
export FINGUARD_JWT_SECRET='segredo-local-com-entropia'
./mvnw -pl services/payment-service -am test
```

Os testes cobrem dominio, contexto, outbox e autorizacao HTTP.

## Limites

Emissao de tokens, rotacao de chaves e integracao com um provedor de identidade ainda nao fazem parte do servico.

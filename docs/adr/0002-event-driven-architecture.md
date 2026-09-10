# ADR 0002: Arquitetura orientada a eventos

## Status

Aceita

## Contexto

Fraude e disputas exigem rastreabilidade, processamento assincrono e tolerancia a falhas.

## Decisao

Kafka sera o backbone de eventos entre servicos. APIs sincronas serao usadas apenas para comandos e consultas apropriadas ao servico dono dos dados.

## Consequencias

- Eventos viram contratos de integracao.
- Consumidores precisam ser idempotentes.
- Falhas devem ir para retry e DLT em fases futuras.

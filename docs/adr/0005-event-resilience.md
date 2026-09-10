# ADR 0005: Resiliencia de eventos

## Status

Aceita

## Contexto

O FinGuard AI depende de eventos para conectar transacoes, fraude, IA, disputas e notificacoes. Uma falha temporaria no Kafka ou em um consumidor nao pode apagar eventos nem duplicar efeitos de negocio.

## Decisao

Usaremos outbox transacional nos servicos que produzem eventos a partir de mudancas de banco. O publisher da outbox registra tentativas, ultimo erro e estado final quando o limite de tentativas e excedido.

Consumidores Kafka devem ser idempotentes. Quando uma mensagem nao puder ser processada apos retry com backoff, ela sera enviada para uma DLT.

## Consequencias

- Eventos importantes sobrevivem a falhas temporarias do broker.
- Reprocessamentos nao criam casos duplicados para a mesma transacao.
- DLTs tornam falhas visiveis e investigaveis.
- Metricas ajudam a detectar acumulacao de eventos pendentes, falhas de publicacao e mensagens rejeitadas.

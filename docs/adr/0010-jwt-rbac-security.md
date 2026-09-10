# ADR 0010: Segurança com JWT e RBAC

## Status

Aceita

## Contexto

Os endpoints financeiros precisam autenticar consumidores e restringir operações administrativas por função. A solução deve funcionar como resource server e não conceder autoridade por configuração implícita.

## Decisão

Os serviços que usam `common-observability` podem ativar um resource server JWT com `security.jwt.enabled=true`. O segredo HMAC vem de `FINGUARD_JWT_SECRET`. A claim `roles` é convertida para authorities `ROLE_*`.

Health checks e informações operacionais são públicos. Métricas Prometheus exigem `ROLE_OPS`, caminhos administrativos exigem `ROLE_ADMIN` e os demais endpoints exigem autenticação. O `payment-service` ativa essa proteção por padrão.

## Consequências

- A autenticação é verificável localmente sem um provedor externo.
- A autorização por função fica explícita e pode ser testada por endpoint.
- O segredo de desenvolvimento não deve ser usado fora do ambiente local.
- Rotação de chaves, issuer externo e gestão de identidade ficam para uma evolução posterior.
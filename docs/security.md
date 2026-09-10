# Runbook de segurança

## Configuração local

Defina um segredo antes de iniciar o `payment-service`:

```bash
export FINGUARD_JWT_SECRET='substitua-por-um-segredo-local-com-boa-entropia'
```

O token JWT deve conter uma claim `roles`, por exemplo `['PAYMENT']`. Para consultar métricas, inclua `OPS`; para caminhos administrativos, inclua `ADMIN`.

## Regras atuais

- `/actuator/health/**` e `/actuator/info`: públicos;
- `/actuator/prometheus`: exige `ROLE_OPS`;
- `/admin/**`: exige `ROLE_ADMIN`;
- demais endpoints protegidos: exigem um JWT válido.

## Limites

O slice atual usa segredo HMAC obrigatório e não implementa login, emissão de tokens, rotação automática ou integração com um provedor de identidade. `FINGUARD_JWT_SECRET` deve ser definido antes de iniciar o `payment-service`.
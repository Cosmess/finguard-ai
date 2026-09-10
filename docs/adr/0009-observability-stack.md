# ADR 0009: Stack de observabilidade local

## Status

Aceita

## Contexto

Os serviços já expõem health checks e algumas métricas Micrometer, mas não havia uma forma local e uniforme de coletar, visualizar e operar esses sinais.

## Decisão

Todos os serviços exporão métricas no endpoint `/actuator/prometheus`. O ambiente local usará Prometheus para coleta e Grafana para visualização. A biblioteca `common-observability` fornecerá o filtro de correlação HTTP, preservando ou gerando `X-Correlation-Id`.

OpenTelemetry e exportação distribuída ficam preparados para uma etapa posterior; a Fase 10 não exige um collector externo para funcionar.

## Consequências

- Métricas operacionais podem ser consultadas localmente sem credenciais externas.
- Correlação de requisições é consistente entre os serviços que usam a biblioteca comum.
- Prometheus e Grafana passam a fazer parte do Compose local.
- O runbook define os primeiros sinais a verificar durante incidentes.
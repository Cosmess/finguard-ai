# Runbook de observabilidade

## Subir a stack

```bash
docker compose up -d prometheus grafana
```

Prometheus fica disponível em `http://localhost:9090` e Grafana em `http://localhost:3000`.

## Verificar um serviço

```bash
curl http://localhost:8086/actuator/health
curl http://localhost:8086/actuator/prometheus
```

O endpoint de métricas deve responder com séries Micrometer. As requisições HTTP devolvem `X-Correlation-Id`; ao enviar esse header, o mesmo valor deve ser preservado na resposta.

## Primeira triagem

1. Verificar saúde do serviço afetado.
2. Consultar erros e latência no endpoint Prometheus.
3. Usar o `X-Correlation-Id` para localizar a requisição nos logs.
4. Conferir Kafka, PostgreSQL e Redis antes de reiniciar componentes.

## Limites

O Compose local não configura autenticação do Grafana nem retenção de longo prazo. Esses controles devem ser definidos antes de qualquer ambiente compartilhado.
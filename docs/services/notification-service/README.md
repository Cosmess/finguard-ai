# notification-service

## Responsabilidade

Ponto reservado para notificacoes operacionais de fraude, disputas e revisao humana. Nesta etapa o modulo fornece o bootstrap Spring Boot e o health check, sem fluxo de envio implementado.

## Arquitetura

A estrutura segue o padrao de servico independente do monorepo: aplicacao Spring Boot, configuracao por ambiente, Actuator e futura separacao entre casos de uso, adaptadores de canais e persistencia.

## Integracoes planejadas

- Entrada futura: eventos Kafka de fraude, disputas e decisoes;
- Saida futura: canais de notificacao e registros de entrega;
- Banco reservado: `finguard_notification`;
- Porta local: `8088`;
- Dependencias locais atuais: nenhuma alem do runtime Spring.

## Observabilidade

Expone `/actuator/health` e `/actuator/prometheus`. O servico deve receber `X-Correlation-Id` quando os endpoints de notificacao forem adicionados.

## Execucao e testes

```bash
./mvnw -pl services/notification-service -am test
```

## Limites

Ainda nao ha consumidores Kafka, templates, canais, retry de entrega ou persistencia de notificacoes. O modulo esta documentado como base para a proxima evolucao funcional.

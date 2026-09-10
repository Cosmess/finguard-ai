# fraud-ai-service

## Responsabilidade

Investiga eventos suspeitos e produz recomendacoes estruturadas. A implementacao atual e um stub deterministico: explica os sinais recebidos e encaminha casos para revisao ou coleta de contexto, sem mover dinheiro ou tomar a decisao financeira.

## Arquitetura

- `adapter/in/kafka`: consumidor de `fraud.suspected`;
- `application`: regra deterministica de investigacao e contrato de recomendacao;
- `adapter/out/persistence`: auditoria JPA das recomendacoes;
- Flyway: tabela `fraud_investigation_audits` e evidencias.

## Integracoes

- Entrada Kafka: `fraud.suspected`.
- Saida: auditoria no banco; a recomendacao pode ser consumida por uma etapa futura de decisao.
- Banco: `finguard_fraud_ai`.
- Dependencias locais: Kafka e PostgreSQL.
- Conhecimento: a evolucao deve consultar o `knowledge-service`, mantendo citacoes.

## Regras atuais

- `HIGH`: `REVIEW_MANUALLY`;
- `MEDIUM`: `COLLECT_MORE_CONTEXT`;
- `LOW`: `NO_ACTION`.

Os sinais do evento sao tratados como evidencias somente leitura.

## Observabilidade e testes

Expone `/actuator/health` e `/actuator/prometheus`. Os testes cobrem recomendacoes estruturadas, rejeicao de risco desconhecido e contexto Spring.

```bash
./mvnw -pl services/fraud-ai-service -am test
```

## Limites

Nao ha provedor externo de modelo, chave de API ou ferramenta de escrita. A integracao de modelo deve permanecer auditavel e sem autoridade transacional.

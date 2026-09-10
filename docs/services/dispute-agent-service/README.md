# dispute-agent-service

## Responsabilidade

Organiza contexto de disputas e orienta analistas. O agente usa LangChain4j para declarar ferramentas somente leitura e nunca altera estados, cria evidencias, aprova disputas ou executa chargebacks.

## Arquitetura

- `application`: contexto, recomendacao e orquestrador deterministico;
- `tools`: ferramentas `@Tool` de leitura de status e evidencias;
- `adapter`: cliente HTTP somente leitura para o `dispute-service`;
- LangChain4j: metadados das ferramentas, sem modelo externo nesta etapa.

## Integracoes

- Leitura HTTP: `GET /disputes/{id}` no `dispute-service`.
- URL configuravel por `DISPUTE_SERVICE_URL` ou `dispute.agent.dispute-service-url`.
- Banco: nenhum banco proprio nesta fase.
- Dependencias locais: `dispute-service` para contexto; `knowledge-service` e uma integracao futura para citacoes.

## Orquestracao

- contexto indisponivel: `REQUEST_CONTEXT`;
- disputa sem evidencias: `REQUEST_EVIDENCE`;
- evidencias presentes: `REVIEW_MANUALLY`.

Falhas HTTP sao convertidas em contexto `UNKNOWN`, evitando que o agente invente dados.

## Observabilidade e testes

Expone `/actuator/health` e `/actuator/prometheus`. Os testes cobrem as decisoes do orquestrador e o contexto injetado para teste.

```bash
./mvnw -pl services/dispute-agent-service -am test
```

## Limites

A conexao com modelo externo ainda nao e necessaria. Qualquer futura ferramenta de escrita deve passar por uma decisao arquitetural separada.

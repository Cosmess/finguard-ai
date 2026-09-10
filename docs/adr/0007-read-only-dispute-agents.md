# ADR 0007: Agentes de disputa somente leitura

## Status

Aceita

## Contexto

Agentes podem organizar evidências e orientar analistas, mas não devem alterar disputas nem executar decisões financeiras. A orquestração precisa ser explícita, auditável e testável sem depender de um provedor externo de modelos.

## Decisão

Usaremos LangChain4j para declarar ferramentas do `dispute-agent-service`. As ferramentas da primeira etapa serão somente leitura e anotadas com `@Tool`. Um orquestrador determinístico decidirá entre solicitar contexto, solicitar evidências ou encaminhar para revisão humana.

O agente não terá ferramentas para criar evidências, mudar estados, aprovar disputas ou executar chargebacks. A integração com modelos externos ficará para uma etapa posterior, atrás de uma porta configurável e sem credenciais em CI.

## Consequências

- O comportamento inicial é reproduzível e coberto por testes sem rede.
- As permissões das ferramentas ficam visíveis no código e podem ser revisadas isoladamente.
- A recomendação do agente continua subordinada à revisão humana.
- Um modelo externo poderá ser conectado depois sem conceder autoridade transacional.
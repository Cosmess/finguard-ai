# ADR 0008: Agentes de disputa somente leitura

## Status

Aceita

## Contexto

O `dispute-agent-service` organiza contexto e evidências para analistas. Ele não pode alterar disputas, aprovar chargebacks ou executar qualquer ação financeira. A primeira implementação também precisa ser reproduzível sem credenciais de provedor externo.

## Decisão

Usaremos LangChain4j para declarar ferramentas de consulta com `@Tool`. O orquestrador determina explicitamente se deve solicitar contexto, solicitar evidências ou encaminhar o caso para revisão humana.

As ferramentas não terão operações de escrita. Modelos externos poderão ser conectados em uma etapa posterior, mas não receberão autoridade transacional e não serão necessários nos testes ou no CI.

## Consequências

- O comportamento inicial é determinístico, auditável e testável sem rede.
- Os limites de permissão ficam visíveis nas ferramentas do agente.
- Toda decisão efetiva continua sob responsabilidade de um fluxo humano ou determinístico autorizado.
- O ADR 0007 permanece como registro histórico da primeira formulação; este ADR é o registro oficial numerado da Fase 8.
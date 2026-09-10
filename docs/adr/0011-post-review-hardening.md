# ADR 0011: Hardening pós-revisão

## Status

Aceita

## Contexto

A revisão do projeto identificou três lacunas: dependências de segurança propagadas para serviços que não as ativavam, o agente de disputas sem leitura de contexto em runtime e documentos sem embeddings preenchidos.

## Decisão

As dependências e a configuração JWT/RBAC ficam no `payment-service`, que é o serviço financeiro protegido. A biblioteca comum permanece restrita a observabilidade e correlação.

O `dispute-agent-service` consulta o `dispute-service` por HTTP GET somente leitura, com URL configurável. O agente mantém a recomendação `REQUEST_CONTEXT` quando a consulta falha.

O `knowledge-service` gera embeddings determinísticos de três dimensões, persiste os valores e combina cobertura lexical com similaridade vetorial no ranking. Nenhuma chamada externa é necessária.

## Consequências

- Serviços não financeiros não recebem autenticação implícita por dependência transitiva.
- O agente passa a operar sobre contexto real quando o `dispute-service` está disponível.
- A busca tem representação semântica reproduzível e preparada para a evolução para pgvector nativo.
- Testes HTTP cobrem health público e endpoint financeiro sem token.
# knowledge-service

## Responsabilidade

Mantem documentos de conhecimento e fornece contexto citavel para servicos de IA. A busca atual combina cobertura lexical com embeddings deterministas persistidos, sem dependencia de provedor externo.

## Arquitetura

- `application`: ingestao, embedding e ranking;
- `adapter/in/web`: endpoints REST;
- `adapter/out/persistence`: entidades e repositorio JPA;
- Flyway: documento, coluna pgvector preparada e `embedding_values`.

## Integracoes

- `POST /knowledge/documents`: recebe titulo, fonte e conteudo;
- `GET /knowledge/search?query=...&limit=...`: retorna trechos e citacoes;
- Banco: `finguard_knowledge`;
- Consumidores: `fraud-ai-service` e `dispute-agent-service` em evolucoes futuras;
- Dependencia local: PostgreSQL com pgvector.

## Busca

Cada documento recebe um embedding deterministico de tres dimensoes. O ranking combina cobertura de termos e similaridade entre os embeddings do documento e da consulta. A resposta sempre carrega `documentId`, titulo, trecho e fonte.

## Observabilidade e testes

Expone `/actuator/health` e `/actuator/prometheus`. Os testes cobrem ingestao, ranking, citacao e limites de busca.

```bash
./mvnw -pl services/knowledge-service -am test
```

## Limites

O embedding atual e deterministico e local; ainda nao representa embeddings semanticos produzidos por um modelo. A coluna `vector(3)` prepara a evolucao para consulta nativa pgvector.

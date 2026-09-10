# ADR 0001: Monorepo Maven

## Status

Aceita

## Contexto

O FinGuard AI sera construido em fases, com varios servicos Spring Boot e bibliotecas pequenas compartilhadas.

## Decisao

Usaremos um monorepo Maven com modulos para `libs` e `services`.

## Consequencias

- O build inicial roda com um unico comando: `./mvnw clean verify`.
- Contratos compartilhados ficam versionados junto dos servicos.
- Cada servico continua tendo sua propria aplicacao Spring Boot e fronteira clara.

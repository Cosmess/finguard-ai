# ADR 0004: Ownership de dados por servico

## Status

Aceita

## Contexto

Servicos com banco compartilhado tendem a acoplar regras internas e dificultar evolucao independente.

## Decisao

Cada servico sera dono do seu proprio banco ou schema. Em ambiente local, um unico container PostgreSQL pode hospedar varios bancos separados.

## Consequencias

- Um servico nao consulta tabelas internas de outro servico.
- Integracao entre servicos acontece por APIs publicas ou eventos.
- Migracoes pertencem ao servico que possui os dados.

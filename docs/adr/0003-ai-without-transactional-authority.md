# ADR 0003: IA sem autoridade transacional

## Status

Aceita

## Contexto

O projeto usa IA para investigacao de fraude e disputas, mas decisoes financeiras precisam ser explicaveis, auditaveis e controladas.

## Decisao

A IA nao pode mover dinheiro, aprovar chargebacks, bloquear contas ou executar reembolsos. Ela produz recomendacoes estruturadas, com evidencias e citacoes quando aplicavel.

## Consequencias

- A decisao final passa por regras deterministicas e, quando necessario, revisao humana.
- Toda resposta de IA precisa ser validada e auditada.
- Chaves e provedores externos nao entram no CI.

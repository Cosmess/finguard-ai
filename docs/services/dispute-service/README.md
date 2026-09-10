# dispute-service

## Responsabilidade

Gerencia o ciclo de vida de disputas financeiras, evidencias e revisao humana. O servico registra a decisao de revisao, mas nao executa chargeback, reembolso ou alteracao financeira.

## Arquitetura

- `application`: agregado `Dispute`, estados e casos de uso;
- `adapter/in/web`: API REST;
- `adapter/out/persistence`: entidades JPA de disputas e evidencias;
- Flyway: tabelas `disputes` e `dispute_evidence`.

Estados suportados: `OPEN`, `EVIDENCE_REQUESTED`, `UNDER_REVIEW`, `RESOLVED` e `REJECTED`.

## Integracoes

Endpoints:

- `POST /disputes`: abre uma disputa;
- `GET /disputes/{id}`: consulta disputa e evidencias;
- `POST /disputes/{id}/evidence-request`: solicita evidencias;
- `POST /disputes/{id}/evidence`: anexa evidencia;
- `POST /disputes/{id}/review`: registra revisao humana.

- Banco: `finguard_dispute`.
- Consumidores: `dispute-agent-service` consulta por GET.
- Dependencias locais: PostgreSQL.

## Regras de transicao

Evidencias podem ser anexadas em `OPEN` ou `EVIDENCE_REQUESTED`, levando o caso a `UNDER_REVIEW`. A revisao exige `reviewerId` e `APPROVE` ou `REJECT`; o resultado vira `RESOLVED` ou `REJECTED`.

## Observabilidade e testes

Expone `/actuator/health` e `/actuator/prometheus`. Os testes cobrem abertura, evidencias, revisao e transicao invalida.

```bash
./mvnw -pl services/dispute-service -am test
```

## Limites

Nao publica ainda um evento de disputa para todos os consumidores downstream. A autoridade financeira continua fora deste servico.

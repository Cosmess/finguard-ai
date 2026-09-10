# decision-service

## Responsabilidade

Aplica politicas deterministicas sobre score de fraude, risco, recomendacao da IA e status de disputa. Persiste uma decisao auditavel, sem delegar a autoridade financeira a um modelo de IA.

## Arquitetura

- `application`: `DecisionRequest`, `DecisionPolicyService`, outcomes e sinais;
- `adapter/in/web`: endpoint REST;
- `adapter/out/persistence`: entidades e repositorio JPA;
- Flyway: tabelas `decisions` e `decision_signals`.

## Integracoes

- Entrada: `POST /decisions`.
- Futuras entradas: eventos ou adaptadores para fraude, IA e disputas.
- Banco: `finguard_decision`.
- Dependencias locais: PostgreSQL.

## Politica atual

Se score for pelo menos 70, risco for `HIGH` ou a IA recomendar `REVIEW_MANUALLY`, o resultado e `HOLD_FOR_REVIEW`. Sem sinal de escalonamento, o resultado e `APPROVE`. Os sinais que justificam o resultado sao persistidos.

## Observabilidade e testes

Expone `/actuator/health` e `/actuator/prometheus`. Os testes cobrem baixo risco, alto risco e contexto Spring.

```bash
./mvnw -pl services/decision-service -am test
```

## Limites

`REJECT` existe no contrato, mas a politica atual nao o produz automaticamente. A revisao humana e o caminho seguro para casos retidos.

package com.cosmess.finguard.payment.adapter.in.web;

import com.cosmess.finguard.payment.application.PaymentService;
import com.cosmess.finguard.payment.application.TransactionNotFoundException;
import com.cosmess.finguard.payment.domain.PaymentMethod;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
class PaymentControllerIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void createsTransactionPersistsOutboxEventAndGetsTransactionById() {
        PaymentController controller = new PaymentController(paymentService);
        CreateTransactionRequest request = new CreateTransactionRequest(
                "merchant-1",
                "customer-1",
                new BigDecimal("129.90"),
                "brl",
                PaymentMethod.CREDIT_CARD,
                "device-1",
                "127.0.0.1"
        );

        TransactionResponse createResponse = controller.create(request);

        assertThat(createResponse.id()).isNotNull();
        assertThat(createResponse.currency()).isEqualTo("BRL");
        assertThat(createResponse.status().name()).isEqualTo("CREATED");

        TransactionResponse getResponse = controller.getById(createResponse.id());

        assertThat(getResponse.id()).isEqualTo(createResponse.id());
        assertThat(getResponse.merchantId()).isEqualTo("merchant-1");

        Integer outboxCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM outbox_events WHERE aggregate_id = ? AND event_type = 'TransactionCreated' AND status = 'PENDING'",
                Integer.class,
                createResponse.id()
        );

        assertThat(outboxCount).isEqualTo(1);
    }

    @Test
    void rejectsInvalidTransactionRequest() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        CreateTransactionRequest request = new CreateTransactionRequest(
                "",
                "customer-1",
                BigDecimal.ZERO,
                "BRL",
                PaymentMethod.CREDIT_CARD,
                null,
                null
        );

        assertThat(validator.validate(request)).hasSize(2);
    }

    @Test
    void returnsNotFoundForUnknownTransaction() {
        PaymentController controller = new PaymentController(paymentService);
        UUID unknownId = UUID.randomUUID();

        assertThatThrownBy(() -> controller.getById(unknownId))
                .isInstanceOf(TransactionNotFoundException.class);
    }
}

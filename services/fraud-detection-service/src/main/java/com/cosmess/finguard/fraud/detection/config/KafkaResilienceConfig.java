package com.cosmess.finguard.fraud.detection.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
class KafkaResilienceConfig {

    @Bean
    CommonErrorHandler commonErrorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate,
            MeterRegistry meterRegistry,
            @Value("${fraud.detection.dlt-topic}") String dltTopic,
            @Value("${fraud.detection.retry.max-attempts}") long maxAttempts,
            @Value("${fraud.detection.retry.backoff}") java.time.Duration backoff
    ) {
        Counter dltCounter = Counter.builder("finguard.kafka.messages.dlt")
                .tag("service", "fraud-detection-service")
                .register(meterRegistry);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> dltDestination(record, dltTopic, dltCounter)
        );

        long retryAttempts = Math.max(0, maxAttempts - 1);
        return new DefaultErrorHandler(recoverer, new FixedBackOff(backoff.toMillis(), retryAttempts));
    }

    private TopicPartition dltDestination(ConsumerRecord<?, ?> record, String dltTopic, Counter dltCounter) {
        dltCounter.increment();
        return new TopicPartition(dltTopic, record.partition());
    }
}

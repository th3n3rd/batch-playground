package com.example.batch.payment.client;

import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import org.springframework.cloud.square.retrofit.EnableRetrofitClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRetrofitClients(
    clients = PaymentApiClient.class,
    defaultConfiguration = DefaultClientConfig.class
)
class PaymentConfig {

    public final static String ResiliencyBackend = "payment";

    @Bean
    public RetryConfigCustomizer paymentRetryConfigCustomizer() {
        return RetryConfigCustomizer.of(
            ResiliencyBackend,
            builder -> builder
                .maxAttempts(5)
                .ignoreExceptions(ResultSetTooLargeException.class)
        );
    }

}

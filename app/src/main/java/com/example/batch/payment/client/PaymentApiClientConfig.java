package com.example.batch.payment.client;

import org.springframework.cloud.square.retrofit.EnableRetrofitClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRetrofitClients(
    clients = PaymentApiClient.class,
    defaultConfiguration = DefaultClientConfig.class
)
class PaymentApiClientConfig {}

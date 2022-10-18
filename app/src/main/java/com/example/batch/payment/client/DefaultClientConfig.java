package com.example.batch.payment.client;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;

@Slf4j
class DefaultClientConfig {

    @Bean
    Interceptor loggingInterceptor() {
        var logging = new HttpLoggingInterceptor(log::info);
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

}

package com.example.batch;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.micrometer.NewRelicRegistry;
import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import java.net.InetAddress;
import java.time.Duration;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("newrelic")
@Configuration
@EnableConfigurationProperties(NewRelicConfig.Props.class)
class NewRelicConfig {

    @ConfigurationProperties(prefix = "newrelic")
    record Props(
        String apiKey,
        Duration step,
        String serviceName,
        String uri
    ) implements NewRelicRegistryConfig {
        @Override
        public String get(String key) {
            return null;
        }
    }

    @Bean
    @SneakyThrows
    public NewRelicRegistry newRelicMeterRegistry(NewRelicRegistryConfig config) {
        NewRelicRegistry newRelicRegistry = NewRelicRegistry
            .builder(config)
            .commonAttributes(new Attributes().put("host", InetAddress.getLocalHost().getHostName()))
            .build();
        newRelicRegistry.config().meterFilter(MeterFilter.denyNameStartsWith("jvm.threads"));
        newRelicRegistry.start(new NamedThreadFactory("newrelic.micrometer.registry"));
        return newRelicRegistry;
    }

}

package com.example.batch.payment.importing.scheduling;

import com.example.batch.payment.importing.ImportJobConfig;
import com.example.batch.utils.Jobs;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;

@Profile("scheduler")
@Configuration
@RequiredArgsConstructor
@EnableIntegration
class RemoteSchedulingConfig {

    @Bean
    public RemoteJobs remoteJobs() {
        return new RemoteJobs(requestsChannel(), repliesChannel());
    }

    @Bean
    public MessageChannel requestsChannel() {
        return new DirectChannel();
    }

    @Bean
    public TopicExchange jobExchange() {
        return new TopicExchange(ImportJobConfig.JobName);
    }

    @Bean
    public IntegrationFlow requestsOutgoingFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlows
            .from(requestsChannel())
            .log()
            .handle(Amqp.outboundAdapter(amqpTemplate)
                .exchangeName(jobExchange().getName())
                .routingKey("requests"))
            .get();
    }

    @Bean
    public Binding repliesFromExchange() {
        return BindingBuilder
            .bind(repliesQueue())
            .to(jobExchange())
            .with("replies");
    }

    @Bean
    public IntegrationFlow repliesIncomingFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlows
            .from(Amqp.inboundAdapter(connectionFactory, repliesQueue()))
            .log()
            .channel(repliesChannel())
            .get();
    }

    @Bean
    public Queue repliesQueue() {
        return new Queue("%s-replies".formatted(ImportJobConfig.JobName));
    }

    @Bean
    public PollableChannel repliesChannel() {
        return new QueueChannel();
    }
}

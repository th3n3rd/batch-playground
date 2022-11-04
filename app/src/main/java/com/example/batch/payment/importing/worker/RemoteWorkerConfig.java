package com.example.batch.payment.importing.worker;

import com.example.batch.payment.importing.ImportJobConfig;
import com.example.batch.utils.Jobs;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageChannel;

@Profile("worker")
@Configuration
@RequiredArgsConstructor
@EnableIntegration
class RemoteWorkerConfig {

    private final Jobs jobs;
    private final JobLauncher jobLauncher;

    @Bean
    public Binding requestsFromExchange() {
        return BindingBuilder
            .bind(requestsQueue())
            .to(jobExchange())
            .with("requests");
    }

    @Bean
    public IntegrationFlow requestsIncomingFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlows
            .from(Amqp.inboundAdapter(connectionFactory, requestsQueue()))
            .log()
            .transform(importJobToJobLaunchRequest())
            .handle(jobLaunchingGateway())
            .get();
    }

    @Bean
    public TopicExchange jobExchange() {
        return new TopicExchange(ImportJobConfig.JobName);
    }

    @Bean
    public Queue requestsQueue() {
        return new Queue("%s-requests".formatted(ImportJobConfig.JobName));
    }

    @Bean
    public ImportJobToJobLaunchRequest importJobToJobLaunchRequest() {
        return new ImportJobToJobLaunchRequest(jobs.findByName(ImportJobConfig.JobName));
    }

    @Bean
    public JobLaunchingGateway jobLaunchingGateway() {
        var gateway = new JobLaunchingGateway(jobLauncher);
        gateway.setOutputChannel(repliesChannel());
        return gateway;
    }

    @Bean
    public MessageChannel repliesChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow repliesOutgoingFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlows
            .from(repliesChannel())
            .log()
            .handle(Amqp.outboundAdapter(amqpTemplate)
                .exchangeName(jobExchange().getName())
                .routingKey("replies"))
            .get();
    }
}

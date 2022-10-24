package com.example.batch.payment.importing;

import com.example.batch.payment.Transaction;
import com.example.batch.payment.client.RawTransactions;
import com.example.batch.payment.importing.FetchPaginatedRawTransactions;
import com.example.batch.payment.importing.PartitionByDateIntervals;
import com.example.batch.payment.importing.PersistProcessedTransactions;
import com.example.batch.payment.importing.StandardiseRawTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilderFactory;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageChannel;

@Profile("remote")
@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
@EnableBatchIntegration
public class RemotePartitionedJobConfig {

    static final String JobName = "import-transactions-remote-partitioned";
    public static final String ManagerStepName = "%s:manager".formatted(JobName);
    public final static String WorkerStepName = "%s:worker".formatted(JobName);

    @Profile({"remote", "master"})
    @Configuration
    @RequiredArgsConstructor
    static class ManagerConfig {

        private final RemotePartitioningManagerStepBuilderFactory steps;
        private final JobBuilderFactory jobs;

        @Bean
        Job importTransactionsJob(@Qualifier("manager") Step manager) {
            return jobs.get(JobName)
                .start(manager)
                .build();
        }

        @Bean
        public Step manager(
            PartitionByDateIntervals partitionByDateIntervals
        ) {
            return steps.get(ManagerStepName)
                .partitioner("worker", partitionByDateIntervals) // TODO: ask spring-batch team why this works with the bean name and not the step name
                .outputChannel(outboundChannel())
                .gridSize(1)
                .taskExecutor(new SyncTaskExecutor())
                .build();
        }

        @Bean
        public MessageChannel outboundChannel() {
            return new DirectChannel();
        }

        @Bean
        public IntegrationFlow outboundFlow(AmqpTemplate messagingTemplate) {
            return IntegrationFlows
                .from(outboundChannel())
                .log()
                .handle(Amqp.outboundAdapter(messagingTemplate).exchangeName(JobName))
                .get();
        }
    }

    @Profile({"remote", "worker"})
    @Configuration
    @RequiredArgsConstructor
    static class WorkerConfig {

        private final static String WorkerId = "%s-%s".formatted(WorkerStepName, 1);
        private final RemotePartitioningWorkerStepBuilderFactory steps;

        @Bean
        public TopicExchange inboundExchange() {
            return new TopicExchange(JobName);
        }

        @Bean
        public Queue inboundQueue() {
            return new Queue(WorkerId);
        }

        @Bean
        public Binding binding() {
            return BindingBuilder
                .bind(inboundQueue())
                .to(inboundExchange())
                .with("#");
        }

        @Bean
        public IntegrationFlow inboundFlow(ConnectionFactory connectionFactory) {
            return IntegrationFlows
                .from(Amqp.inboundAdapter(connectionFactory, inboundQueue()))
                .log()
                .channel(inboundChannel())
                .get();
        }

        @Bean
        public QueueChannel inboundChannel() {
            return new QueueChannel();
        }

        @Bean
        public Step worker(
            FetchPaginatedRawTransactions fetchPaginatedRawTransactions,
            StandardiseRawTransaction standardiseRawTransaction,
            PersistProcessedTransactions persistProcessedTransactions
        ) {
            return steps.get(WorkerStepName)
                .inputChannel(inboundChannel())
                .<RawTransactions.Detail, Transaction>chunk(100)
                .reader(fetchPaginatedRawTransactions)
                .processor(standardiseRawTransaction)
                .writer(persistProcessedTransactions)
                .build();
        }
    }
}

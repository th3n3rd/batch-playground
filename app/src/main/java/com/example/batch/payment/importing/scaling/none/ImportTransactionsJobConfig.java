package com.example.batch.payment.importing.scaling.none;

import com.example.batch.payment.Transaction;
import com.example.batch.payment.client.RawTransactions;
import com.example.batch.payment.importing.FetchPaginatedRawTransactions;
import com.example.batch.payment.importing.PartitionByDateIntervals;
import com.example.batch.payment.importing.PersistProcessedTransactions;
import com.example.batch.payment.importing.StandardiseRawTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class ImportTransactionsJobConfig {

    private final JobBuilderFactory jobs;

    static final String Name = "import-transactions-scaling-none";

    @Bean
    Job importTransactionsJob(@Qualifier("manager") Step manager) {
        return jobs.get(Name)
            .start(manager)
            .build();
    }

    @Configuration
    @RequiredArgsConstructor
    static class ManagerConfig {

        private final StepBuilderFactory steps;

        @Bean
        public Step manager(
            PartitionByDateIntervals partitionByDateIntervals,
            @Qualifier("worker") Step worker
        ) {
            return steps.get("%s:manager".formatted(Name))
                .partitioner(worker.getName(), partitionByDateIntervals)
                .step(worker)
                .gridSize(1)
                .taskExecutor(new SyncTaskExecutor())
                .build();
        }
    }

    @Configuration
    @RequiredArgsConstructor
    static class WorkerConfig {

        private final StepBuilderFactory steps;

        @Bean
        public Step worker(
            FetchPaginatedRawTransactions fetchPaginatedRawTransactions,
            StandardiseRawTransaction standardiseRawTransaction,
            PersistProcessedTransactions persistProcessedTransactions
        ) {
            return steps.get("%s:worker".formatted(Name))
                .<RawTransactions.Detail, Transaction>chunk(100)
                .reader(fetchPaginatedRawTransactions)
                .processor(standardiseRawTransaction)
                .writer(persistProcessedTransactions)
                .build();
        }
    }
}

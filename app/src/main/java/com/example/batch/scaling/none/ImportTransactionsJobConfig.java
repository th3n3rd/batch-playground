package com.example.batch.scaling.none;

import com.example.batch.payment.Transaction;
import com.example.batch.payment.client.RawTransactions;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
class ImportTransactionsJobConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;

    static final String Name = "import-transactions-scaling-none";

    @Bean
    org.springframework.batch.core.Job importTransactionsJob(
        PartitionByDateIntervals partitionByDateIntervals,
        FetchPaginatedRawTransactions fetchPaginatedRawTransactions,
        StandardiseRawTransaction standardiseRawTransaction,
        PersistProcessedTransactions persistProcessedTransactions
    ) {
        var slave = steps.get("%s:slave".formatted(Name))
            .<RawTransactions.Detail, Transaction>chunk(100)
            .reader(fetchPaginatedRawTransactions)
            .processor(standardiseRawTransaction)
            .writer(persistProcessedTransactions)
            .build();

        var master = steps.get("%s:master".formatted(Name))
            .partitioner(slave.getName(), partitionByDateIntervals)
            .step(slave)
            .gridSize(1)
            .taskExecutor(new SyncTaskExecutor())
            .build();

        return jobs.get(Name)
            .start(master)
            .build();
    }
}

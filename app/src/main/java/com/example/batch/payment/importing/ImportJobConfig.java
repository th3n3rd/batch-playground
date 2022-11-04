package com.example.batch.payment.importing;

import com.example.batch.payment.Transaction;
import com.example.batch.payment.client.RawTransactions;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class ImportJobConfig {

    public static final String JobName = "import-transactions";

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;

    @Bean
    Job importTransactionsJob(@Qualifier("manager") Step manager) {
        return jobs.get(JobName)
            .start(manager)
            .build();
    }

    @Bean
    public Step manager(
        PartitionByDateIntervals partitionByDateIntervals,
        @Qualifier("worker") Step worker
    ) {
        return steps.get("%s:manager".formatted(JobName))
            .partitioner(worker.getName(), partitionByDateIntervals)
            .step(worker)
            .gridSize(1)
            .taskExecutor(new SyncTaskExecutor())
            .build();
    }

    @Bean
    public Step worker(
        FetchPaginatedRawTransactions fetchPaginatedRawTransactions,
        StandardiseRawTransaction standardiseRawTransaction,
        PersistProcessedTransactions persistProcessedTransactions,
        @Qualifier("workerTaskExecutor") TaskExecutor workerTaskExecutor
    ) {
        return steps.get("%s:worker".formatted(JobName))
            .<RawTransactions.Detail, Transaction>chunk(100)
            .reader(fetchPaginatedRawTransactions)
            .processor(standardiseRawTransaction)
            .writer(persistProcessedTransactions)
            .taskExecutor(workerTaskExecutor)
            .build();
    }

    @Bean
    public TaskExecutor workerTaskExecutor(@Value("${import-job.thread-pool.size:1}") int threadPoolSize) {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolSize);
        executor.initialize();
        return executor;
    }
}

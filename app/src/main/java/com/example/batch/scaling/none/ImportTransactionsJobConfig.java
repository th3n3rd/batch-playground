package com.example.batch.scaling.none;

import com.example.batch.payment.Transaction;
import com.example.batch.payment.client.RawTransactions;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
class ImportTransactionsJobConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;

    static final String Name = "import-transactions-scaling-none";

    @Bean
    org.springframework.batch.core.Job importTransactionsJob(
        ExtractPaginatedRawTransactions extract,
        TransformRawTransactions transform,
        LoadProcessedTransactions load
    ) {
        return jobs.get(Name)
            .start(extractTransformLoadStep(extract, transform, load))
            .build();
    }

    Step extractTransformLoadStep(
        ExtractPaginatedRawTransactions extract,
        TransformRawTransactions transform,
        LoadProcessedTransactions load
    ) {
        return steps.get("extract-transform-load-step")
            .<RawTransactions.Detail, Transaction>chunk(1)
            .reader(extract)
            .processor(transform)
            .writer(load)
            .build();
    }

}

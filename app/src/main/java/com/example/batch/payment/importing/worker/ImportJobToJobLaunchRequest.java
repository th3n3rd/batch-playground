package com.example.batch.payment.importing.worker;

import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.integration.transformer.GenericTransformer;

@RequiredArgsConstructor
class ImportJobToJobLaunchRequest implements GenericTransformer<ImportJob, JobLaunchRequest> {

    private final Job job;

    @Override
    public JobLaunchRequest transform(ImportJob source) {
        return new JobLaunchRequest(
            job,
            new JobParameters(Map.of(
                "accountId", new JobParameter(source.accountId),
                "nonce", new JobParameter(UUID.randomUUID().toString())
            ))
        );
    }
}

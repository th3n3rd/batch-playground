package com.example.batch.payment.importing;

import com.example.batch.payment.client.MerchantAccountDetail;
import com.example.batch.payment.importing.ImportTransactionsJourneyTests;
import com.example.batch.utils.Jobs;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import lombok.SneakyThrows;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = { "newrelic", "local" })
class LocallyPartitionedJourneyTests extends ImportTransactionsJourneyTests {

    @Autowired
    private Jobs jobs;

    @Autowired
    private JobLauncher jobLauncher;

    @Override
    @SneakyThrows
    protected void importTransactions(MerchantAccountDetail account) {
        jobLauncher.run(jobs.findByName(LocallyPartitionedJobConfig.JobName), new JobParameters(Map.of(
            "accountId", new JobParameter(account.accountInfo.accountId),
            "nonce", new JobParameter(UUID.randomUUID().toString())
        )));
    }

}

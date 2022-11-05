package com.example.batch.payment.importing;

import static org.awaitility.Awaitility.await;

import com.example.batch.payment.client.MerchantAccountDetail;
import com.example.batch.payment.importing.scheduling.RemoteJobs;
import com.example.batch.payment.importing.worker.ImportJob;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = { "scheduler", "postgres" })
class RemoteJobJourneyTests extends ImportTransactionsJourneyTests {

    @Autowired
    private RemoteJobs remoteJobs;

    @Override
    @SneakyThrows
    protected void importTransactions(MerchantAccountDetail account) {
        remoteJobs.schedule(new ImportJob(account.accountInfo.accountId));
        await()
            .atMost(300, TimeUnit.SECONDS)
            .until(() -> remoteJobs.lastJobFinished());
    }

    @Test
    void importAllRandomSmallAccounts() {
        var accounts = allSmallAccountsFromSource();
        accounts.forEach(it -> remoteJobs.schedule(new ImportJob(it.accountInfo.accountId)));
        await()
            .atMost(300, TimeUnit.SECONDS)
            .until(() -> remoteJobs.lastNJobsFinished(accounts.size()));
        accounts.forEach(this::assertAllTransactionsHaveBeenImported);
    }
}

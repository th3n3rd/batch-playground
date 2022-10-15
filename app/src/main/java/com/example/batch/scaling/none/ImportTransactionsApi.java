package com.example.batch.scaling.none;

import com.example.batch.utils.Jobs;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
class ImportTransactionsApi {

    private final Jobs jobs;
    private final JobLauncher jobLauncher;

    @SneakyThrows
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/scaling/none/accounts/{accountId}/transactions")
    void importTransactions(@PathVariable String accountId) {
        jobLauncher.run(jobs.findByName(ImportTransactionsJobConfig.Name), new JobParameters(Map.of(
            "accountId", new JobParameter(accountId)
        )));
    }

}

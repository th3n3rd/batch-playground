package com.example.batch.scaling.none;

import com.example.batch.payment.Interval;
import com.example.batch.payment.NonOverlappingIntervals;
import com.example.batch.payment.client.PaymentApiClient;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
class PartitionByDateIntervals implements Partitioner {

    private final PaymentApiClient paymentApiClient;
    private final String accountId;
    private final int maxIntervalInDays;

    public PartitionByDateIntervals(
        PaymentApiClient paymentApiClient,
        @Value("#{jobParameters['accountId']}") String accountId,
        @Value("${external-system.payment.max-interval-days:31}") int maxIntervalInDays
    ) {
        this.paymentApiClient = paymentApiClient;
        this.accountId = accountId;
        this.maxIntervalInDays = maxIntervalInDays;
    }

    @Override
    public Map<String, ExecutionContext> partition(int partitions) {
        var contextByPartition = new HashMap<String, ExecutionContext>();
        computeImportIntervals().forEach(it -> {
            var startDate = it.getStartDate().toString();
            var endDate = it.getEndDate().toString();
            var partitionKey = "%s|%s".formatted(startDate, endDate);
            contextByPartition.put(partitionKey, new ExecutionContext(
                Map.of(
                    "accountId", accountId,
                    "startDate", startDate,
                    "endDate", endDate
                )
            ));
        });
        return contextByPartition;
    }

    private List<Interval> computeImportIntervals() {
        return new NonOverlappingIntervals(
            accountCreationDate(),
            OffsetDateTime.now(),
            maxIntervalInDays
        ).toList();
    }

    @SneakyThrows
    private OffsetDateTime accountCreationDate() {
        var account = paymentApiClient.accountDetails(accountId).execute().body();
        return OffsetDateTime.parse(account.accountInfo.createdAt);
    }
}

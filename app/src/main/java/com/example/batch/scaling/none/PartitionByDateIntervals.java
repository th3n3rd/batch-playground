package com.example.batch.scaling.none;

import com.example.batch.payment.ImportIntervals;
import com.example.batch.payment.Interval;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
class PartitionByDateIntervals implements Partitioner {

    private final ImportIntervals importIntervals;
    private final String accountId;
    private final int maxIntervalInDays;

    public PartitionByDateIntervals(
        ImportIntervals importIntervals,
        @Value("#{jobParameters['accountId']}") String accountId,
        @Value("${external-system.payment.max-interval-days:31}") int maxIntervalInDays
    ) {
        this.importIntervals = importIntervals;
        this.accountId = accountId;
        this.maxIntervalInDays = maxIntervalInDays;
    }

    @Override
    public Map<String, ExecutionContext> partition(int partitions) {
        return partitionByIntervals(
            importIntervals.findAllBy(accountId, maxIntervalInDays)
        );
    }

    @NotNull
    private HashMap<String, ExecutionContext> partitionByIntervals(List<Interval> intervals) {
        var contextByPartition = new HashMap<String, ExecutionContext>();
        for (int i = 0; i < intervals.size(); i++) {
            Interval it = intervals.get(i);
            var startDate = it.getStartDate().toString();
            var endDate = it.getEndDate().toString();
            var partitionKey = "partition-%s".formatted(i);
            contextByPartition.put(
                partitionKey,
                new ExecutionContext(Map.of("accountId", accountId, "startDate", startDate, "endDate", endDate))
            );
        }
        return contextByPartition;
    }
}

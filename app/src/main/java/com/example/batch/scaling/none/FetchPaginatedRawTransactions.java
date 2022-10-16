package com.example.batch.scaling.none;

import com.example.batch.payment.client.PaymentApiClient;
import com.example.batch.payment.client.RawTransactions;
import java.util.Iterator;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.AbstractPaginatedDataItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@StepScope
@Component
class FetchPaginatedRawTransactions extends AbstractPaginatedDataItemReader<RawTransactions.Detail> {

    private final PaymentApiClient paymentApiClient;
    private final String accountId;
    private final String startDate;
    private final String endDate;

    public FetchPaginatedRawTransactions(
        @Value("#{stepExecutionContext['accountId']}") String accountId,
        @Value("#{stepExecutionContext['startDate']}") String startDate,
        @Value("#{stepExecutionContext['endDate']}") String endDate,
        PaymentApiClient paymentApiClient
    ) {
        setName("extract-paginated-raw-transactions");
        this.startDate = startDate;
        this.endDate = endDate;
        this.accountId = accountId;
        this.paymentApiClient = paymentApiClient;
    }

    @Override
    protected Iterator<RawTransactions.Detail> doPageRead() {
        var rawTransactions = extractTransactions(startDate, endDate);
        return Objects.isNull(rawTransactions)
            ? null
            : rawTransactions.transactionDetails.iterator();
    }

    @SneakyThrows
    private RawTransactions extractTransactions(String fromAccountCreation, String untilNow) {
        return paymentApiClient
            .listTransactions(
                accountId, fromAccountCreation, untilNow,
                page,
                pageSize
            )
            .execute()
            .body();
    }
}

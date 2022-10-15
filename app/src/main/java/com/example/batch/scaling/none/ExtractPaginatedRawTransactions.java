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
class ExtractPaginatedRawTransactions extends AbstractPaginatedDataItemReader<RawTransactions.Detail> {

    private final PaymentApiClient paymentApiClient;
    private final String accountId;

    public ExtractPaginatedRawTransactions(
        @Value("#{jobParameters['accountId']}") String accountId,
        PaymentApiClient paymentApiClient
    ) {
        setName("extract-paginated-raw-transactions");
        this.accountId = accountId;
        this.paymentApiClient = paymentApiClient;
    }

    @SneakyThrows
    @Override
    protected Iterator<RawTransactions.Detail> doPageRead() {
        var responseBody = paymentApiClient
            .listTransactions(
                accountId,
                "2022-09-01T00:00Z",
                "2022-09-30T00:00Z",
                page,
                pageSize
            )
            .execute()
            .body();

        return Objects.isNull(responseBody)
            ? null
            : responseBody.transactionDetails.iterator();
    }
}

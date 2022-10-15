package com.example.batch.scaling.none;

import com.example.batch.payment.client.PaymentApiClient;
import com.example.batch.payment.client.RawTransactions;
import java.time.OffsetDateTime;
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
        var account = paymentApiClient.accountDetails(accountId).execute().body();
        var fromAccountCreation = OffsetDateTime.parse(account.accountInfo.createdAt).toString();
        var untilNow = OffsetDateTime.now().toString();
        var responseBody = paymentApiClient
            .listTransactions(
                accountId,
                fromAccountCreation,
                untilNow,
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

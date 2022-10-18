package com.example.batch.payment.importing;

import com.example.batch.payment.client.PaymentService;
import com.example.batch.payment.client.RawTransactions;
import java.util.Iterator;
import java.util.List;
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
public class FetchPaginatedRawTransactions extends AbstractPaginatedDataItemReader<RawTransactions.Detail> {

    private final PaymentService paymentService;
    private final String accountId;
    private final String startDate;
    private final String endDate;

    public FetchPaginatedRawTransactions(
        @Value("#{stepExecutionContext['accountId']}") String accountId,
        @Value("#{stepExecutionContext['startDate']}") String startDate,
        @Value("#{stepExecutionContext['endDate']}") String endDate,
        PaymentService paymentService
    ) {
        setName("extract-paginated-raw-transactions");
        setPageSize(100);
        this.startDate = startDate;
        this.endDate = endDate;
        this.accountId = accountId;
        this.paymentService = paymentService;
    }

    @Override
    protected Iterator<RawTransactions.Detail> doPageRead() {
        return extractTransactions(startDate, endDate).iterator();
    }

    @SneakyThrows
    private List<RawTransactions.Detail> extractTransactions(String fromAccountCreation, String untilNow) {
        return paymentService.listTransactions(
            accountId, fromAccountCreation, untilNow,
            page,
            pageSize
        );
    }
}

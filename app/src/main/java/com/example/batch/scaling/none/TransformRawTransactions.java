package com.example.batch.scaling.none;

import com.example.batch.payment.Transaction;
import com.example.batch.payment.client.RawTransactions;
import java.util.UUID;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
class TransformRawTransactions implements ItemProcessor<RawTransactions.Detail, Transaction> {

    private final String accountId;

    public TransformRawTransactions(@Value("#{jobParameters['accountId']}") String accountId) {
        this.accountId = accountId;
    }

    @Override
    public Transaction process(RawTransactions.Detail item) {
        return new Transaction(
            UUID.fromString(item.transactionInfo.transactionId),
            UUID.fromString(accountId),
            item.transactionInfo.transactionAmount.currencyCode,
            item.transactionInfo.transactionAmount.value
        );
    }
}

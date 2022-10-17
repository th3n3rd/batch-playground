package com.example.batch.payment.importing;

import com.example.batch.payment.Transaction;
import com.example.batch.payment.client.RawTransactions;
import java.util.UUID;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StandardiseRawTransaction implements ItemProcessor<RawTransactions.Detail, Transaction> {

    @Override
    public Transaction process(RawTransactions.Detail item) {
        return new Transaction(
            UUID.fromString(item.transactionInfo.transactionId),
            UUID.fromString(item.transactionInfo.merchantAccountId),
            item.transactionInfo.transactionAmount.currencyCode,
            item.transactionInfo.transactionAmount.value
        );
    }
}

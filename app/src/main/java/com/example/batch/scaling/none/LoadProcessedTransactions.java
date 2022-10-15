package com.example.batch.scaling.none;

import com.example.batch.payment.Transaction;
import com.example.batch.payment.Transactions;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LoadProcessedTransactions implements ItemWriter<Transaction> {

    private final Transactions transactions;

    @Override
    public void write(List<? extends Transaction> items) {
        transactions.saveAll(items);
    }
}

package com.example.batch.payment.importing;

import com.example.batch.payment.client.MerchantAccountDetail;
import com.example.batch.payment.client.PaymentService;
import com.example.batch.payment.client.ResultSetTooLargeException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportIntervals {

    private final PaymentService paymentService;

    public List<Interval> findAllBy(String accountId, int maxIntervalInDays) {
        var account = accountDetail(accountId);

        var potentialIntervals = new Stack<Interval>();
        potentialIntervals.addAll(
            Interval.between(
                OffsetDateTime.parse(account.accountInfo.createdAt),
                OffsetDateTime.now()
            ).split(maxIntervalInDays)
        );

        var validatedIntervals = new ArrayList<Interval>();
        while (!potentialIntervals.isEmpty()) {
            var interval = potentialIntervals.pop();
            if (isResultSetTooLarge(account, interval)) {
                potentialIntervals.addAll(interval.splitInHalf());
            } else {
                validatedIntervals.add(interval);
            }
        }

        return validatedIntervals;
    }

    @SneakyThrows
    private MerchantAccountDetail accountDetail(String accountId) {
        return paymentService.accountDetail(accountId);
    }

    @SneakyThrows
    private boolean isResultSetTooLarge(MerchantAccountDetail account, Interval interval) {
        try {
            paymentService.listTransactions(
                account.accountInfo.accountId,
                interval.getStartDate().toString(),
                interval.getEndDate().toString(),
                1,
                1
            );
            return false;
        } catch (ResultSetTooLargeException e) {
            return true;
        }
    }
}

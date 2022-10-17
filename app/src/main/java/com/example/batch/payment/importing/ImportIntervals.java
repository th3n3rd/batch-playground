package com.example.batch.payment.importing;

import com.example.batch.payment.client.Errors;
import com.example.batch.payment.client.MerchantAccountDetail;
import com.example.batch.payment.client.PaymentApiClient;
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

    private final PaymentApiClient paymentApiClient;

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
        return paymentApiClient.accountDetails(accountId).execute().body();
    }

    @SneakyThrows
    private boolean isResultSetTooLarge(MerchantAccountDetail account, Interval interval) {
        var response = paymentApiClient
            .listTransactions(
                account.accountInfo.accountId,
                interval.getStartDate().toString(),
                interval.getEndDate().toString(),
                1,
                1
            )
            .execute();

        if (response.isSuccessful()) {
            return false;
        }

        return Errors.isResultSetTooLarge(response);
    }
}

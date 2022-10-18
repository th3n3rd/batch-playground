package com.example.batch.payment.client;

import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentApiClient apiClient;

    @Retry(name = "listAccounts")
    @SneakyThrows
    public List<MerchantAccountDetail> listAccounts() {
        var response = apiClient.listAccounts().execute();

        if (response.isSuccessful()) {
            return response.body().accountDetails;
        }

        throw new UnknownPaymentErrorException();
    }

    @Retry(name = "accountDetail")
    @SneakyThrows
    public MerchantAccountDetail accountDetail(String accountId) {
        var response = apiClient.accountDetails(accountId).execute();

        if (response.isSuccessful()) {
            return response.body();
        }

        throw new UnknownPaymentErrorException();
    }

    @Retry(name = "listTransactions")
    @SneakyThrows
    public List<RawTransactions.Detail> listTransactions(
        String accountId,
        String startDate,
        String endDate,
        int page,
        int pageSize
    ) {
        var response = apiClient.listTransactions(
            accountId,
            startDate,
            endDate,
            page,
            pageSize
        )
        .execute();

        if (response.isSuccessful()) {
            return response.body().transactionDetails;
        }

        if (Errors.isResultSetTooLarge(response)) {
            throw new ResultSetTooLargeException();
        }

        throw new UnknownPaymentErrorException();
    }

    @Retry(name = "countTransactions")
    @SneakyThrows
    public long countTransactions(
        String accountId,
        String startDate,
        String endDate
    ) {
        var response = apiClient.countTransactions(
            accountId,
            startDate,
            endDate
        ).execute();

        if (response.isSuccessful()) {
            return response.body().totalItems;
        }

        throw new UnknownPaymentErrorException();
    }
}

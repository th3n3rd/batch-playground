package com.example.batch.payment.client;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentApiClient apiClient;

    @SneakyThrows
    public List<MerchantAccountDetail> listAccounts() {
        return apiClient.listAccounts()
            .execute()
            .body()
            .accountDetails;
    }

    @SneakyThrows
    public MerchantAccountDetail accountDetail(String accountId) {
        return apiClient.accountDetails(accountId)
            .execute()
            .body();
    }

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

    @SneakyThrows
    public long countTransactions(
        String accountId,
        String startDate,
        String endDate
    ) {
        return apiClient.countTransactions(
                accountId,
                startDate,
                endDate
            )
            .execute()
            .body()
            .totalItems;
    }
}

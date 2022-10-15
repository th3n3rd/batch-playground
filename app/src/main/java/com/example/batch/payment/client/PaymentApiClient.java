package com.example.batch.payment.client;

import org.springframework.cloud.square.retrofit.core.RetrofitClient;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

@RetrofitClient(
    name = "external-system-payment",
    url = "${external-system.payment.url}"
)
public interface PaymentApiClient {
    @GET("/v1/payment/accounts")
    Call<MerchantAccounts> listAccounts();

    @GET("/v1/payment/accounts/{accountId}")
    Call<MerchantAccounts.Detail> accountDetails(
        @Path("accountId") String accountId
    );

    @GET("/v1/payment/accounts/{accountId}/transactions")
    Call<RawTransactions> listTransactions(
        @Path("accountId") String accountId,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate
    );

    @GET("/v1/payment/accounts/{accountId}/transactions")
    Call<RawTransactions> listTransactions(
        @Path("accountId") String accountId,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate,
        @Query("page") int page,
        @Query("pageSize") int pageSize
    );

    @GET("/v1/payment/accounts/{accountId}/transactions/count")
    Call<RawTransactionsCount> countTransactions(
        @Path("accountId") String accountId,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate
    );
}

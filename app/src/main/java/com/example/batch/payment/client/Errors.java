package com.example.batch.payment.client;

import lombok.SneakyThrows;
import retrofit2.Response;

public class Errors {
    @SneakyThrows
    public static boolean isResultSetTooLarge(Response response) {
        return new String(response.errorBody().bytes()).contains("Result set too large");
    }
}

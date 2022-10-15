package com.example.batch.payment.client;

import java.util.List;
import lombok.AllArgsConstructor;

public class CalculatedBalances {
    public List<Balance> balances;

    @AllArgsConstructor
    public static class Balance {
        public String currencyCode;
        public Double value;
    }
}

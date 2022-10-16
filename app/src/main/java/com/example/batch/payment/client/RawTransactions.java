package com.example.batch.payment.client;

import java.util.List;
import java.util.UUID;

public class RawTransactions {
    public long totalItems;
    public int totalPages;
    public List<Detail> transactionDetails;

    public static class Detail {
        public Info transactionInfo;
    }

    public static class Info {
        public String transactionId;
        public String merchantAccountId;
        public Amount transactionAmount;
    }

    public static class Amount {
        public String currencyCode;
        public Double value;
    }
}

package com.example.batch.payment.client;

import java.util.List;

public class RawTransactions {
    public long totalItems;
    public int totalPages;
    public List<Detail> transactionDetails;

    public static class Detail {
        public Info transactionInfo;
    }

    public static class Info {
        public String transactionId;
        public Amount transactionAmount;
    }

    public static class Amount {
        public String currencyCode;
        public Double value;
    }
}

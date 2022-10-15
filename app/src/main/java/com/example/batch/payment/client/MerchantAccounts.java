package com.example.batch.payment.client;

import java.util.List;

public class MerchantAccounts {
    public List<Detail> accountDetails;

    public static class Detail {
        public Info accountInfo;
    }

    public static class Info {
        public String accountId;
        public String createdAt;
    }
}

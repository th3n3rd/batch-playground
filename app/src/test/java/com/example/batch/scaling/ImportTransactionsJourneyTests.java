package com.example.batch.scaling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.example.batch.payment.Transactions;
import com.example.batch.payment.client.MerchantAccountDetail;
import com.example.batch.payment.client.PaymentApiClient;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@Tag("e2e")
@Slf4j
@SpringBootTest(webEnvironment = RANDOM_PORT)
abstract public class ImportTransactionsJourneyTests {

    @Autowired
    protected WebTestClient client;

    @Autowired
    protected PaymentApiClient paymentApiClient;

    @Autowired
    private Transactions transactions;

    private final static String largeAccountId = "1a77305c-6404-4438-bb0c-274921184596";

    @Test
    void importRandomSmallAccount() {
        var randomSmallAccount = randomSmallAccountFromSource();
        importTransactions(randomSmallAccount);
        assertThat(countTransactions(randomSmallAccount))
            .isGreaterThan(0)
            .isEqualTo(countTransactionsFromSource(randomSmallAccount));
    }

    @Test
    void importLargeAccount() {
        var largeAccount = largeAccountFromSource();
        importTransactions(largeAccount);
        assertThat(countTransactions(largeAccount))
            .isGreaterThan(0)
            .isEqualTo(countTransactionsFromSource(largeAccount));
    }

    @SneakyThrows
    private long countTransactionsFromSource(MerchantAccountDetail account) {
        var fromAccountCreation = account.accountInfo.createdAt;
        var untilNow = OffsetDateTime.now().toString();
        return paymentApiClient
            .countTransactions(
                account.accountInfo.accountId,
                fromAccountCreation,
                untilNow
            )
            .execute()
            .body()
            .totalItems;
    }

    private long countTransactions(MerchantAccountDetail account) {
        return transactions.countAllByAccountId(UUID.fromString(account.accountInfo.accountId));
    }

    @SneakyThrows
    private MerchantAccountDetail randomSmallAccountFromSource() {
        return paymentApiClient.listAccounts()
            .execute()
            .body()
            .accountDetails
            .stream()
            .filter(it -> !it.accountInfo.accountId.equals(largeAccountId))
            .findAny()
            .orElseThrow();
    }

    @SneakyThrows
    private MerchantAccountDetail largeAccountFromSource() {
        return paymentApiClient.accountDetails(largeAccountId)
            .execute()
            .body();
    }

    abstract protected void importTransactions(MerchantAccountDetail account);
}

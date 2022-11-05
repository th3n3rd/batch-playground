package com.example.batch.payment.importing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.example.batch.payment.Transactions;
import com.example.batch.payment.client.ExternalPaymentService;
import com.example.batch.payment.client.MerchantAccountDetail;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@Tag("e2e")
@Slf4j
@SpringBootTest(webEnvironment = RANDOM_PORT)
abstract public class ImportTransactionsJourneyTests {

    @Autowired
    protected WebTestClient client;

    @Autowired
    protected ExternalPaymentService externalPaymentService;

    @Autowired
    private Transactions transactions;

    private final static String largeAccountId = "1a77305c-6404-4438-bb0c-274921184596";

    @BeforeEach
    void setUp() {
        transactions.truncate();
    }

    @Test
    void importRandomSmallAccount() {
        var randomSmallAccount = randomSmallAccountFromSource();
        importTransactions(randomSmallAccount);
        assertAllTransactionsHaveBeenImported(randomSmallAccount);
    }

    @Test
    void importLargeAccount() {
        var largeAccount = largeAccountFromSource();
        importTransactions(largeAccount);
        assertAllTransactionsHaveBeenImported(largeAccount);
    }

    protected List<MerchantAccountDetail> allSmallAccountsFromSource() {
        return externalPaymentService
            .listAccounts()
            .stream()
            .filter(it -> !it.accountInfo.accountId.equals(largeAccountId))
            .collect(Collectors.toList());
    }

    protected void assertAllTransactionsHaveBeenImported(MerchantAccountDetail largeAccount) {
        assertThat(countTransactions(largeAccount))
            .isGreaterThan(0)
            .isEqualTo(countTransactionsFromSource(largeAccount));
    }

    @SneakyThrows
    private MerchantAccountDetail randomSmallAccountFromSource() {
        return allSmallAccountsFromSource()
            .stream()
            .findAny()
            .orElseThrow();
    }

    @SneakyThrows
    private MerchantAccountDetail largeAccountFromSource() {
        return externalPaymentService.accountDetail(largeAccountId);
    }

    @SneakyThrows
    private long countTransactionsFromSource(MerchantAccountDetail account) {
        var fromAccountCreation = account.accountInfo.createdAt;
        var untilNow = OffsetDateTime.now().toString();
        return externalPaymentService.countTransactions(
            account.accountInfo.accountId,
            fromAccountCreation,
            untilNow
        );
    }

    private long countTransactions(MerchantAccountDetail account) {
        return transactions.countAllByAccountId(UUID.fromString(account.accountInfo.accountId));
    }

    abstract protected void importTransactions(MerchantAccountDetail account);
}

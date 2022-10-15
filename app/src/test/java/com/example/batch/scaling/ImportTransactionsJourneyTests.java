package com.example.batch.scaling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.example.batch.payment.Transactions;
import com.example.batch.payment.client.MerchantAccounts;
import com.example.batch.payment.client.PaymentApiClient;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
    protected PaymentApiClient paymentApiClient;

    @Autowired
    private Transactions transactions;

    @Test
    void importGivenAccount() {
        var account = randomAccountIdFromSource();
        importTransactions(account);
        assertThat(countTransactions(account))
            .isGreaterThan(0)
            .isEqualTo(countTransactionsFromSource(account));
    }

    @SneakyThrows
    private long countTransactionsFromSource(MerchantAccounts.Detail account) {
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

    private long countTransactions(MerchantAccounts.Detail account) {
        return transactions.countAllByAccountId(UUID.fromString(account.accountInfo.accountId));
    }

    @SneakyThrows
    private MerchantAccounts.Detail randomAccountIdFromSource() {
        return paymentApiClient.listAccounts()
            .execute()
            .body()
            .accountDetails
            .stream()
            .findAny()
            .orElseThrow();
    }

    abstract protected void importTransactions(MerchantAccounts.Detail account);
}

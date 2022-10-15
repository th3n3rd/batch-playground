package com.example.externalsystem.payment;

import static com.example.externalsystem.payment.ListTransactionsApi.MaxDateRangeInDays;

import com.github.javafaker.Faker;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class TestDataLoader implements ApplicationRunner {

    private final MerchantAccounts accounts;
    private final Transactions transactions;

    @Override
    public void run(ApplicationArguments args) {
        var faker = new Faker();
        var testAccounts = new ArrayList<MerchantAccount>();
        var testTransactions = new ArrayList<Transaction>();
        var numberOfAccounts = 100;
        var transactionsPerAccount = 100;
        var minAmount = -1000;
        var maxAmount = 1000;
        var pastDate = OffsetDateTime.parse("2022-01-01T00:00:00+00:00");
        var presentDate = pastDate.plusDays(MaxDateRangeInDays + 1);
        var futureDate = presentDate.plusDays(MaxDateRangeInDays + 1);
        for (int i = 0; i < numberOfAccounts; i++) {
            var account = new MerchantAccount(
                UUID.randomUUID(),
                randomDateBetween(faker, pastDate, presentDate)
            );
            testAccounts.add(account);
            for (int j = 0; j < transactionsPerAccount; j++) {
                testTransactions.add(
                    new Transaction(
                        UUID.randomUUID(),
                        account.getAccountId(),
                        new Amount(
                            "USD",
                            randomAmountValue(faker, minAmount, maxAmount)
                        ),
                        randomDateBetween(faker, presentDate, futureDate)
                    ));
            }
        }
        accounts.saveAllAndFlush(testAccounts);
        transactions.saveAllAndFlush(testTransactions);
        log.info("Loaded %s accounts for testing".formatted(numberOfAccounts));
        log.info("Loaded a total of %s transactions for testing, %s per account".formatted(numberOfAccounts * transactionsPerAccount, transactionsPerAccount));
        log.info("The first loaded account id for testing was %s".formatted(testAccounts.get(0).getAccountId()));
        log.info("Transactions time range span from %s to %s".formatted(presentDate, futureDate));
    }

    private static Double randomAmountValue(Faker faker, int minAmount, int maxAmount) {
        return faker.number().randomDouble(2, minAmount, maxAmount);
    }

    private static OffsetDateTime randomDateBetween(Faker faker, OffsetDateTime startDate, OffsetDateTime endDate) {
        var timeOffsetInHours = faker.random().nextLong(Duration.between(startDate, endDate).toHours());
        var updateDate = startDate.plus(timeOffsetInHours, ChronoUnit.HOURS);
        return updateDate;
    }
}

package com.example.externalsystem.payment;

import static com.example.externalsystem.payment.ListTransactionsApi.MaxDateRangeInDays;

import com.github.javafaker.Faker;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
    private final static UUID LargeAccountId = UUID.fromString("1a77305c-6404-4438-bb0c-274921184596");;

    @Override
    public void run(ApplicationArguments args) {
        var faker = new Faker();
        var numberOfSmallAccounts = 100;
        var transactionsPerSmallAccount = 1000;
        var minAmount = -1000;
        var maxAmount = 1000;
        var now = OffsetDateTime.now();
        var accountLatestDate = now.minusDays(MaxDateRangeInDays * 6);
        var accountsEarliestDate = accountLatestDate.minusDays(MaxDateRangeInDays * 2);

        loadOneLargeAccount(
            faker,
            100_000,
            minAmount,
            maxAmount,
            accountsEarliestDate,
            accountLatestDate
        );

        var firstSmallAccountId = loadManySmallAccounts(
            faker,
            numberOfSmallAccounts,
            transactionsPerSmallAccount,
            minAmount,
            maxAmount,
            accountsEarliestDate,
            accountLatestDate
        );

        log.info("The only large account id for testing is %s".formatted(LargeAccountId));
        log.info("The first small account id for testing is %s".formatted(firstSmallAccountId));
        log.info("Transactions time range span from %s to %s".formatted(accountLatestDate, now));
        log.info("Test data loading is now complete");
    }

    private UUID loadManySmallAccounts(
        Faker faker,
        int numberOfSmallAccounts,
        int transactionsPerAccount,
        int minAmount,
        int maxAmount,
        OffsetDateTime accountsEarliestDate,
        OffsetDateTime accountLatestDate
    ) {
        var smallAccounts = new ArrayList<MerchantAccount>();
        var smallTransactions = new ArrayList<Transaction>();
        log.info("Loading small accounts with %s transactions across many intervals".formatted(transactionsPerAccount));
        for (int i = 0; i < numberOfSmallAccounts; i++) {
            var accountCreation = randomDateBetween(faker, accountsEarliestDate, accountLatestDate);
            var account = new MerchantAccount(
                UUID.randomUUID(),
                accountCreation
            );
            smallAccounts.add(account);
            accumulateTransactions(
                faker,
                account,
                minAmount,
                maxAmount,
                transactionsPerAccount,
                account.getCreatedAt(),
                OffsetDateTime.now(), // spanning across account creation and now
                smallTransactions
            );
        }
        accounts.saveAllAndFlush(smallAccounts);
        transactions.saveAllAndFlush(smallTransactions);
        var firstSmallAccountId = smallAccounts.get(0).getAccountId();
        smallAccounts.clear();
        smallTransactions.clear();
        log.info("Loaded %s small accounts for testing".formatted(numberOfSmallAccounts));
        return firstSmallAccountId;
    }

    private void loadOneLargeAccount(
        Faker faker,
        int numberOfTransactions,
        int minAmount,
        int maxAmount,
        OffsetDateTime accountsEarliestDate,
        OffsetDateTime accountLatestDate
    ) {
        log.info("Loading large account with 10K+ transactions in a date interval");
        var largeTransactions = new ArrayList<Transaction>();
        var accountCreation = randomDateBetween(faker, accountsEarliestDate, accountLatestDate);
        var largeAccount = new MerchantAccount(
            LargeAccountId,
            accountCreation
        );
        accumulateTransactions(faker,
            largeAccount,
            minAmount,
            maxAmount,
            numberOfTransactions,
            largeAccount.getCreatedAt(),
            accountLatestDate.plusDays(MaxDateRangeInDays * 5), // restrict the date range to have 10K+ per interval
            largeTransactions
        );
        accounts.saveAndFlush(largeAccount);
        transactions.saveAllAndFlush(largeTransactions);
        largeTransactions.clear();
        log.info("Large account loaded");
    }

    private static void accumulateTransactions(
        Faker faker,
        MerchantAccount account,
        int minAmount,
        int maxAmount,
        int numberOfTransactions,
        OffsetDateTime transactionEarliest,
        OffsetDateTime transactionLatest,
        List<Transaction> accumulator
    ) {
        for (int i = 0; i < numberOfTransactions; i++) {
            accumulator.add(
                new Transaction(
                    UUID.randomUUID(),
                    account.getAccountId(),
                    new Amount(
                        "USD",
                        randomAmountValue(faker, minAmount, maxAmount)
                    ),
                    randomDateBetween(faker, transactionEarliest, transactionLatest)
                ));
        }
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

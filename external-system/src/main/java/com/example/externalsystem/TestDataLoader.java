package com.example.externalsystem;

import com.github.javafaker.Faker;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
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
        for (int i = 0; i < numberOfAccounts; i++) {
            var account = new MerchantAccount(UUID.randomUUID());
            testAccounts.add(account);
            for (int j = 0; j < transactionsPerAccount; j++) {
                testTransactions.add(
                    new Transaction(
                        UUID.randomUUID(),
                        account.getAccountId(),
                        new Transaction.Amount(
                            "USD",
                            String.valueOf(faker.number().randomDouble(2, minAmount, maxAmount))
                        )
                    ));
            }
        }
        accounts.saveAllAndFlush(testAccounts);
        transactions.saveAllAndFlush(testTransactions);
        log.info("Loaded %s accounts for testing".formatted(numberOfAccounts));
        log.info("Loaded a total of %s transactions for testing, %s per account".formatted(numberOfAccounts * transactionsPerAccount, transactionsPerAccount));
        log.info("The first loaded account id for testing was %s".formatted(testAccounts.get(0).getAccountId()));
    }
}

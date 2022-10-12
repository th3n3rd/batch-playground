package com.example.batch;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBatchTest
@SpringBootTest
class GreetJobTests {

    @Autowired
    private JobLauncherTestUtils jobLauncher;

    @Autowired
    private People people;

    @Autowired
    private Greetings greetings;

    @Test
    @SneakyThrows
    void saysHelloToEverybody() {
        people.saveAll(List.of(
           Person.identifiedBy("SZ659218A", "Alice"),
           Person.identifiedBy("WK892520", "Bob"),
           Person.identifiedBy("ZH976354D", "Charlie")
        ));

        var execution = jobLauncher.launchJob();

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        var allGreetings = greetings
            .findAll()
            .stream()
            .map(Greeting::getMessage)
            .collect(Collectors.toList());

        assertThat(allGreetings).containsExactly(
            "Hello Alice",
            "Hello Bob",
            "Hello Charlie"
        );
    }

}

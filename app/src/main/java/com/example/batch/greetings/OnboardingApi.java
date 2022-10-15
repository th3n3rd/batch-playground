package com.example.batch.greetings;

import com.example.batch.utils.Jobs;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
class OnboardingApi {

    private final Jobs jobs;
    private final JobLauncher jobLauncher;
    private final People people;
    private final Greetings greetings;

    @SneakyThrows
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/people")
    void join(@RequestBody Identity identity) {
        people.save(Person.identifiedBy(
            identity.niNo,
            identity.firstName
        ));
    }

    @SneakyThrows
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/people/all/greetings")
    void greetEverybody() {
        jobLauncher.run(jobs.findByName(GreetJobConfig.Name), new JobParameters());
    }

    @GetMapping("/people/all/greetings")
    List<String> listGreetings() {
        return greetings
            .findAll()
            .stream()
            .map(Greeting::getMessage)
            .collect(Collectors.toList());
    }

    @AllArgsConstructor
    public static class Identity {
        public String niNo;
        public String firstName;
    }
}

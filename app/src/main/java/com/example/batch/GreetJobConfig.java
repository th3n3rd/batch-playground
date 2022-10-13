package com.example.batch;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
class GreetJobConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;

    @Bean
    Job greetJob(People people, Greetings greetings) {
        return jobs.get("greet-job")
            .start(greetStep(people, greetings))
            .build();
    }

    Step greetStep(People people, Greetings greetings) {
        return steps.get("greeting-step")
            .<Person, Greeting>chunk(1)
            .reader(providePeople(people))
            .processor(greeter())
            .writer(persistGreetings(greetings))
            .build();
    }

    ItemReader<Person> providePeople(People people) {
        return new RepositoryItemReaderBuilder<Person>()
            .repository(people)
            .saveState(false) // TODO: read more about this, not sure disabling make sense
            .methodName("findAll")
            .sorts(Map.of(
                "niNo", Sort.Direction.ASC
            ))
            .build();
    }

    ItemProcessor<Person, Greeting> greeter() {
        return (person) -> Greeting.sayHelloTo(person);
    }

    ItemWriter<Greeting> persistGreetings(Greetings greetings) {
        return new RepositoryItemWriterBuilder<Greeting>()
            .repository(greetings)
            .methodName("save")
            .build();
    }
}

package com.example.batch.utils;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Jobs {

    private final List<Job> registeredJobs;

    public Job findByName(String name) {
        return registeredJobs
            .stream()
            .filter(it -> it.getName().equals(name))
            .findFirst()
            .orElseThrow();
    }

}

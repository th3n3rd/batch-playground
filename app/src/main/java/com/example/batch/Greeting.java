package com.example.batch;

import static lombok.AccessLevel.PROTECTED;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
class Greeting {
    @Id
    private UUID id;
    private String message;

    static Greeting sayHelloTo(Person person) {
        return new Greeting(UUID.randomUUID(), "Hello %s".formatted(person.getFirstName()));
    }
}

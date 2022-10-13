package com.example.batch;

import static lombok.AccessLevel.PROTECTED;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Person {
    @Id
    private String niNo;
    private String firstName;

    static Person identifiedBy(String niNo, String firstName) {
        return new Person(niNo, firstName);
    }
}



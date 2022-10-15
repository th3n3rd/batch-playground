package com.example.batch.payment;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Transaction {
    @Id
    @Type(type = "uuid-char")
    private UUID id;

    @Type(type = "uuid-char")
    private UUID accountId;

    private String currencyCode;
    private Double value;
}

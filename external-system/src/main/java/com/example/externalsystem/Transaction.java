package com.example.externalsystem;

import java.util.UUID;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Transaction {
    @Id
    @Type(type = "uuid-char")
    private UUID transactionId;

    @Type(type = "uuid-char")
    private UUID merchantAccountId;

    @Embedded
    public Amount transactionAmount;

    @Embeddable
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Amount {
        private String currencyCode;
        private String value;
    }
}

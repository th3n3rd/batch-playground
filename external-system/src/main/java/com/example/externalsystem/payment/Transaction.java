package com.example.externalsystem.payment;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Table(
    indexes = {
        @Index(name = "idx-merchant-account-id", columnList = "merchantAccountId")
    }
)
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

    private OffsetDateTime transactionUpdateDate;
}

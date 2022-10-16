package com.example.batch.payment;

import java.time.OffsetDateTime;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
public class Interval {
    OffsetDateTime startDate;
    OffsetDateTime endDate;

    public static Interval between(OffsetDateTime startDate, OffsetDateTime endDate) {
        return new Interval(startDate, endDate);
    }
}

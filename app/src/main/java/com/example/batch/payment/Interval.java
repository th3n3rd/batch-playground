package com.example.batch.payment;

import java.time.OffsetDateTime;
import lombok.Value;

@Value(staticConstructor = "between")
public class Interval {
    OffsetDateTime startDate;
    OffsetDateTime endDate;
}

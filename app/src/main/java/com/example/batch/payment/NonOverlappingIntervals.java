package com.example.batch.payment;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NonOverlappingIntervals {
    private final OffsetDateTime from;
    private final OffsetDateTime to;
    private final int intervalMaxLengthInDays;

    public List<Interval> toList() {
        return checkAndSplit(from, to);
    }

    private List<Interval> checkAndSplit(OffsetDateTime startDate, OffsetDateTime endDate) {
        if (sameDate(startDate, endDate)) {
            return List.of();
        }
        if (!requiresSplit(startDate, endDate)) {
            return List.of(Interval.between(startDate, endDate));
        }
        var nextInterval = intervalFrom(startDate);
        var intervals = new ArrayList<Interval>();
        intervals.add(nextInterval);
        intervals.addAll(checkAndSplit(nextInterval.getEndDate().plusSeconds(1), endDate));
        return intervals;
    }

    private Interval intervalFrom(OffsetDateTime startDate) {
        return Interval.between(startDate, startDate.plusDays(intervalMaxLengthInDays));
    }

    private static boolean sameDate(OffsetDateTime startDate, OffsetDateTime endDate) {
        return startDate.isEqual(endDate);
    }

    private boolean requiresSplit(OffsetDateTime startDate, OffsetDateTime endDate) {
        var duration = Duration.between(startDate, endDate);
        return duration.toDays() >= intervalMaxLengthInDays;
    }

}

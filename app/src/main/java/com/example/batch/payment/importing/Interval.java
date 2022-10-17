package com.example.batch.payment.importing;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Value;

@Value
public class Interval {
    OffsetDateTime startDate;
    OffsetDateTime endDate;

    public static Interval between(OffsetDateTime startDate, OffsetDateTime endDate) {
        return new Interval(startDate, endDate);
    }

    public List<Interval> splitInHalf() {
        var duration = Duration.between(startDate, endDate);
        var firstHalf = intervalFrom(startDate, (int) duration.toDays() / 2);
        var secondHalf = Interval.between(firstHalf.endDate.plusSeconds(1), endDate);
        return List.of(firstHalf, secondHalf);
    }

    public List<Interval> split(int intervalLength)  {
        return checkAndSplit(startDate, endDate, intervalLength);
    }

    private List<Interval> checkAndSplit(OffsetDateTime startDate, OffsetDateTime endDate, int intervalLength) {
        if (sameDate(startDate, endDate)) {
            return List.of();
        }
        if (!requiresSplit(startDate, endDate, intervalLength)) {
            return List.of(Interval.between(startDate, endDate));
        }
        var nextInterval = intervalFrom(startDate, intervalLength);
        var intervals = new ArrayList<Interval>();
        intervals.add(nextInterval);
        intervals.addAll(checkAndSplit(nextInterval.getEndDate().plusSeconds(1), endDate, intervalLength));
        return intervals;
    }

    private Interval intervalFrom(OffsetDateTime startDate, int intervalLength) {
        return Interval.between(startDate, startDate.plusDays(intervalLength));
    }

    private static boolean sameDate(OffsetDateTime startDate, OffsetDateTime endDate) {
        return startDate.isEqual(endDate);
    }

    private boolean requiresSplit(OffsetDateTime startDate, OffsetDateTime endDate, double intervalLength) {
        var duration = Duration.between(startDate, endDate);
        return duration.toDays() >= intervalLength;
    }
}

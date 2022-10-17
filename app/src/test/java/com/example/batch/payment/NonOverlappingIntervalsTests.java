package com.example.batch.payment;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.batch.payment.importing.Interval;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class NonOverlappingIntervalsTests {

    @Test
    void emptyIntervalsIfSameDates() {
        var same = date("2022-10-01T00:00:00Z");

        var intervals = Interval.between(same, same).split(10);

        assertThat(intervals).isEmpty();
    }

    @Test
    void noSplitWhenDistanceBelowMaxLength() {
        var from = date("2022-10-01T00:00:00Z");
        var to = date("2022-10-10T00:00:00Z");

        var intervals = Interval.between(from, to).split(10);

        assertThat(intervals).containsOnly(
            Interval.between(from, to)
        );
    }

    @Test
    void splitIntoTwoIntervalsWhenExceedMaxLength() {
        var from = date("2022-10-01T00:00:00Z");
        var to = date("2022-10-11T00:00:00Z");

        var intervals = Interval.between(from, to).split(10);

        assertThat(intervals).containsOnly(
            Interval.between(from, date("2022-10-11T00:00:00Z")),
            Interval.between(date("2022-10-11T00:00:01Z"), to)
        );
    }

    @Test
    void splitIntoManyIntervalsWhenExceedMaxLengthAfterSplitting() {
        var from = date("2022-10-01T00:00:00Z");
        var to = date("2022-10-31T00:00:00Z");

        var intervals = Interval.between(from, to).split(10);

        assertThat(intervals).containsOnly(
            Interval.between(from, date("2022-10-11T00:00:00Z")),
            Interval.between(date("2022-10-11T00:00:01Z"), date("2022-10-21T00:00:01Z")),
            Interval.between(date("2022-10-21T00:00:02Z"), to)
        );
    }

    @Test
    void splitIntoHalf() {
        var from = date("2022-10-01T00:00:00Z");
        var to = date("2022-10-30T00:00:00Z");

        var intervals = Interval.between(from, to).splitInHalf();

        assertThat(intervals).containsOnly(
            Interval.between(from, date("2022-10-15T00:00:00Z")),
            Interval.between(date("2022-10-15T00:00:01Z"), to)
        );
    }

    private static OffsetDateTime date(String text) {
        return OffsetDateTime.parse(text);
    }
}


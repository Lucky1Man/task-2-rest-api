package org.example.task2restapi.validator.datetime.range;

import java.util.List;

public interface ObjectWithDateTimeRanges {
    List<DateTimeRange> getValidatedRanges();
}

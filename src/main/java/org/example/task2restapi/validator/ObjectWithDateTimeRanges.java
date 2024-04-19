package org.example.task2restapi.validator;

import java.util.List;

public interface ObjectWithDateTimeRanges {
    List<DateTimeRange> getValidatedRanges();
}

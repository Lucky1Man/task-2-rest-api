package org.example.task2restapi.validator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DateTimeRange {
    private LocalDateTime from;
    private LocalDateTime to;
    private String fromFieldName;
    private String toFieldName;

    public DateTimeRange(LocalDateTime from, LocalDateTime to) {
        this(from, to, "fromDateTime", "toDateTime");
    }
}

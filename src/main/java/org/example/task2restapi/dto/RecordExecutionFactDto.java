package org.example.task2restapi.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.task2restapi.validator.DateTimeRange;
import org.example.task2restapi.validator.DateTimeRangeConstraint;
import org.example.task2restapi.validator.ObjectWithDateTimeRanges;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DateTimeRangeConstraint
public class RecordExecutionFactDto implements ObjectWithDateTimeRanges {

    @NotNull(message = "Execution fact must have executor id")
    private UUID executorId;

    @NotNull(message = "Execution fact must have description")
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Nullable
    private LocalDateTime startTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Nullable
    private LocalDateTime finishTime;

    @Override
    public List<DateTimeRange> getValidatedRanges() {
        return List.of(
                new DateTimeRange(startTime, finishTime, "startTime", "finishTime")
        );
    }
}

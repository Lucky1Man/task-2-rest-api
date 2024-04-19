package org.example.task2restapi.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.task2restapi.validator.DateTimeRange;
import org.example.task2restapi.validator.DateTimeRangeConstraint;
import org.example.task2restapi.validator.ObjectWithDateTimeRanges;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DateTimeRangeConstraint
public class ExecutionFactFilterOptionsDto implements ObjectWithDateTimeRanges {
    @Nullable
    private String executorEmail;
    @Nullable
    private LocalDateTime fromFinishTime;
    @Nullable
    private LocalDateTime toFinishTime;
    @Nullable
    private String description;
    @Nullable
    private Integer pageIndex;
    @Nullable
    private Integer pageSize;

    @Override
    public List<DateTimeRange> getValidatedRanges() {
        return List.of(
                new DateTimeRange(fromFinishTime, toFinishTime, "fromFinishTime", "toFinishTime")
        );
    }
}

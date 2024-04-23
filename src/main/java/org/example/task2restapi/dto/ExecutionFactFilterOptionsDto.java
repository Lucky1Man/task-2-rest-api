package org.example.task2restapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.task2restapi.validator.DateTimeRange;
import org.example.task2restapi.validator.DateTimeRangeConstraint;
import org.example.task2restapi.validator.ObjectWithDateTimeRanges;
import org.springframework.format.annotation.DateTimeFormat;

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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fromFinishTime;
    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime toFinishTime;
    @Nullable
    private String description;
    @Nullable
    private Integer pageIndex;
    @Nullable
    private Integer pageSize;

    @Override
    @JsonIgnore
    public List<DateTimeRange> getValidatedRanges() {
        return List.of(
                new DateTimeRange(fromFinishTime, toFinishTime, "fromFinishTime", "toFinishTime")
        );
    }
}

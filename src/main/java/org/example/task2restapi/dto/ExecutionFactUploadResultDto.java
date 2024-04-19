package org.example.task2restapi.dto;

import jakarta.validation.ConstraintViolation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionFactUploadResultDto {
    private Integer addedCount;
    private Map<RecordExecutionFactDto, Set<ConstraintViolation<RecordExecutionFactDto>>> objectToItsViolations;
}

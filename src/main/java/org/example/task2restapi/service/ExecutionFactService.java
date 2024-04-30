package org.example.task2restapi.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.task2restapi.dto.ExecutionFactFilterOptionsDto;
import org.example.task2restapi.dto.ExecutionFactUploadResultDto;
import org.example.task2restapi.dto.GetDetailedExecutionFactDto;
import org.example.task2restapi.dto.GetExecutionFactDto;
import org.example.task2restapi.dto.RecordExecutionFactDto;
import org.example.task2restapi.dto.UpdateExecutionFactDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

@Validated
public interface ExecutionFactService {
    UUID recordExecutionFact(@NotNull @Valid RecordExecutionFactDto factDto);

    GetDetailedExecutionFactDto getById(@NotNull UUID id);

    void updateExecutionFact(@NotNull UUID id, @NotNull @Valid UpdateExecutionFactDto factDto);

    void deleteById(@NotNull UUID id);

    List<GetExecutionFactDto> findAll(@NotNull @Valid ExecutionFactFilterOptionsDto factFilterOptionsDto);

    ByteArrayInputStream generateCsvReport(@NotNull @Valid ExecutionFactFilterOptionsDto factFilterOptionsDto);

    ExecutionFactUploadResultDto uploadFromFile(@NotNull MultipartFile multipart);
}

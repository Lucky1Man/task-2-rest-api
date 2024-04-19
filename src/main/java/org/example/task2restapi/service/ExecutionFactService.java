package org.example.task2restapi.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.task2restapi.dto.ExecutionFactFilterOptionsDto;
import org.example.task2restapi.dto.ExecutionFactUploadResultDto;
import org.example.task2restapi.dto.GetDetailedExecutionFactDto;
import org.example.task2restapi.dto.GetExecutionFactDto;
import org.example.task2restapi.dto.RecordExecutionFactDto;
import org.example.task2restapi.dto.UpdateExecutionFactDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Validated
public interface ExecutionFactService {
    UUID recordExecutionFact(@Valid RecordExecutionFactDto factDto);

    GetDetailedExecutionFactDto getById(UUID id);

    void updateExecutionFact(UUID id, @Valid UpdateExecutionFactDto factDto);

    void deleteById(UUID id);

    List<GetExecutionFactDto> findAll(@Valid ExecutionFactFilterOptionsDto factFilterOptionsDto);

    void generateReport(@Valid ExecutionFactFilterOptionsDto factFilterOptionsDto, HttpServletResponse response);

    ExecutionFactUploadResultDto uploadFromFile(MultipartFile multipart);
}

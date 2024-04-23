package org.example.task2restapi.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import org.example.task2restapi.dto.ExecutionFactFilterOptionsDto;
import org.example.task2restapi.dto.ExecutionFactUploadResultDto;
import org.example.task2restapi.dto.GetDetailedExecutionFactDto;
import org.example.task2restapi.dto.GetExecutionFactDto;
import org.example.task2restapi.dto.RecordExecutionFactDto;
import org.example.task2restapi.dto.UpdateExecutionFactDto;
import org.example.task2restapi.service.ExecutionFactService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class ExecutionFactServiceImpl implements ExecutionFactService {
    @Override
    public UUID recordExecutionFact(RecordExecutionFactDto factDto) {
        return null;
    }

    @Override
    public GetDetailedExecutionFactDto getById(UUID id) {
        return null;
    }

    @Override
    public void updateExecutionFact(UUID id, UpdateExecutionFactDto factDto) {

    }

    @Override
    public void deleteById(UUID id) {
    }

    @Override
    public List<GetExecutionFactDto> findAll(ExecutionFactFilterOptionsDto factFilterOptionsDto) {
        return null;
    }

    @Override
    public void generateReport(ExecutionFactFilterOptionsDto factFilterOptionsDto, HttpServletResponse response) {

    }

    @Override
    public ExecutionFactUploadResultDto uploadFromFile(MultipartFile multipart) {
        return null;
    }
}

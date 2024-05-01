package org.example.task2restapi.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.example.task2restapi.controller.ExceptionResponse;
import org.example.task2restapi.dto.ExecutionFactFilterOptionsDto;
import org.example.task2restapi.dto.ExecutionFactUploadResultDto;
import org.example.task2restapi.dto.GetDetailedExecutionFactDto;
import org.example.task2restapi.dto.GetExecutionFactDto;
import org.example.task2restapi.dto.RecordExecutionFactDto;
import org.example.task2restapi.dto.RecordFactToItsValidationExceptions;
import org.example.task2restapi.dto.UpdateExecutionFactDto;
import org.example.task2restapi.entity.ExecutionFact;
import org.example.task2restapi.entity.Participant;
import org.example.task2restapi.repository.ExecutionFactRepository;
import org.example.task2restapi.repository.ParticipantRepository;
import org.example.task2restapi.service.DateTimeService;
import org.example.task2restapi.service.ExecutionFactService;
import org.example.task2restapi.specification.ExecutionFactSpecs;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ExecutionFactServiceImpl implements ExecutionFactService {

    private final ExecutionFactRepository factRepository;

    private final ParticipantRepository participantRepository;

    private final ModelMapper modelMapper;

    private final DateTimeService dateTimeService;

    private final Integer getFactsMaxPageSize;

    private final ExecutionFactSpecs executionFactSpecs;

    private final Validator validator;

    private final ObjectMapper objectMapper;

    public ExecutionFactServiceImpl(ExecutionFactRepository factRepository,
                                    ParticipantRepository participantRepository,
                                    ModelMapper modelMapper,
                                    DateTimeService dateTimeService,
                                    @Qualifier("executionFactsMaxPageSize")
                                    Integer getFactsMaxPageSize,
                                    ExecutionFactSpecs executionFactSpecs,
                                    Validator validator, ObjectMapper objectMapper) {
        this.factRepository = factRepository;
        this.participantRepository = participantRepository;
        this.modelMapper = modelMapper;
        this.dateTimeService = dateTimeService;
        this.getFactsMaxPageSize = getFactsMaxPageSize;
        this.executionFactSpecs = executionFactSpecs;
        this.validator = validator;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void configureModelMapper() {
        modelMapper.emptyTypeMap(RecordExecutionFactDto.class, ExecutionFact.class).addMappings(mapping -> {
            mapping.skip(ExecutionFact::setId);
            mapping.using((MappingContext<UUID, Participant> ctx) -> {
                        UUID id = ctx.getSource();
                        if (id == null) {
                            return null;
                        } else {
                            return getRawParticipantOrThrow(id);
                        }
                    }
            ).map(RecordExecutionFactDto::getExecutorId, ExecutionFact::setExecutor);
            mapping.using((MappingContext<LocalDateTime, LocalDateTime> ctx) -> {
                if (ctx.getSource() == null) {
                    return dateTimeService.utcNow();
                }
                return ctx.getSource();
            }).map(
                    RecordExecutionFactDto::getStartTime,
                    ExecutionFact::setStartTime
            );
        }).implicitMappings();
    }

    private Participant getRawParticipantOrThrow(UUID id) {
        return participantRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Executor with id '%s' not found".formatted(id))
        );
    }

    @Override
    public UUID recordExecutionFact(@NotNull @Valid RecordExecutionFactDto factDto) {
        if (factDto.getFinishTime() != null && factDto.getStartTime() == null) {
            throw new IllegalArgumentException("Start time must be specified if finish time is");
        }
        ExecutionFact executionFact = modelMapper.map(factDto, ExecutionFact.class);
        return factRepository.save(executionFact).getId();
    }

    @Override
    public GetDetailedExecutionFactDto getById(@NotNull UUID id) {
        return modelMapper.map(
                getRawFactOrThrowNotFound(id),
                GetDetailedExecutionFactDto.class
        );
    }

    private ExecutionFact getRawFactOrThrowNotFound(UUID id) {
        return factRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Execution fact with id '%s' not found".formatted(id))
        );
    }

    @Override
    public void updateExecutionFact(@NotNull UUID id, @NotNull @Valid UpdateExecutionFactDto factDto) {
        ExecutionFact fact = getRawFactOrThrowNotFound(id);
        if (factDto.getDescription() != null) {
            fact.setDescription(factDto.getDescription());
        }
        if (factDto.getExecutorId() != null) {
            fact.setExecutor(getRawParticipantOrThrow(factDto.getExecutorId()));
        }
        if (factDto.getStartTime() != null) {
            fact.setStartTime(factDto.getStartTime());
        }
        if (factDto.getFinishTime() != null) {
            fact.setFinishTime(factDto.getFinishTime());
        }
    }

    @Override
    public void deleteById(@NotNull UUID id) {
        factRepository.deleteById(id);
    }

    @Override
    public List<GetExecutionFactDto> findAll(@NotNull @Valid ExecutionFactFilterOptionsDto factFilterOptionsDto) {
        validatePageSize(factFilterOptionsDto);
        assignDefaultValues(factFilterOptionsDto);
        return factRepository.findAll(
                        executionFactSpecs.byFilterDto(factFilterOptionsDto),
                        PageRequest.of(factFilterOptionsDto.getPageIndex(), factFilterOptionsDto.getPageSize())
                ).getContent().stream()
                .map(entity -> modelMapper.map(entity, GetExecutionFactDto.class))
                .toList();
    }

    private void validatePageSize(ExecutionFactFilterOptionsDto factFilterOptionsDto) {
        Integer pageSize = factFilterOptionsDto.getPageSize();
        if (pageSize != null && pageSize > getFactsMaxPageSize) {
            throw new IllegalArgumentException("pageSize: must be less then or equal to %s".formatted(getFactsMaxPageSize));
        }
    }


    private void assignDefaultValues(ExecutionFactFilterOptionsDto factFilterOptionsDto) {
        if (factFilterOptionsDto.getPageIndex() == null) {
            factFilterOptionsDto.setPageIndex(0);
        }
        if (factFilterOptionsDto.getPageSize() == null) {
            factFilterOptionsDto.setPageSize(50);
        }
    }

    @Override
    public ByteArrayInputStream generateCsvReport(@NotNull @Valid ExecutionFactFilterOptionsDto factFilterOptionsDto) {
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT)
        ) {
            for (GetExecutionFactDto fact : findAll(factFilterOptionsDto)) {
                List<String> data = Arrays.asList(
                        String.valueOf(fact.getId()),
                        fact.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME),
                        fact.getFinishTime().format(DateTimeFormatter.ISO_DATE_TIME),
                        fact.getExecutorFullName(),
                        String.valueOf(fact.getExecutorId()),
                        fact.getDescription()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new UncheckedIOException("Fail to import data to CSV file: " + e.getMessage(), e);
        }
    }

    @Override
    public ExecutionFactUploadResultDto uploadFromFile(@NotNull MultipartFile multipart) {
        try {
            byte[] fileBytes = multipart.getBytes();
            int importedCount = 0;
            int failedCount = 0;
            List<RecordFactToItsValidationExceptions> invalidRecordDtosToItsValidationExceptions = new LinkedList<>();
            List<RecordExecutionFactDto> factDtos = objectMapper.readValue(fileBytes, new TypeReference<>() {});
            for(RecordExecutionFactDto dto : factDtos) {
                Optional<RecordFactToItsValidationExceptions> exceptions = resolveFact(dto);
                if(exceptions.isPresent()) {
                    invalidRecordDtosToItsValidationExceptions.add(exceptions.get());
                    failedCount++;
                } else {
                    importedCount++;
                }
            }
            return new ExecutionFactUploadResultDto(importedCount, failedCount, invalidRecordDtosToItsValidationExceptions);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Optional<RecordFactToItsValidationExceptions> resolveFact(RecordExecutionFactDto factDto) {
        try {
            Set<ConstraintViolation<RecordExecutionFactDto>> validate = validator.validate(factDto);
            if(validate.isEmpty()) {
                recordExecutionFact(factDto);
                return Optional.empty();
            }
            return Optional.of(
                    new RecordFactToItsValidationExceptions(
                            factDto, toExceptionResponse(new ConstraintViolationException(validate))
                    )
            );
        } catch (MappingException e) {
            return Optional.of(
                    new RecordFactToItsValidationExceptions(factDto, toExceptionResponse(e.getCause()))
            );
        }
    }

    private ExceptionResponse toExceptionResponse(Throwable e) {
        return ExceptionResponse.builder()
                        .withHttpStatus(HttpStatus.BAD_REQUEST)
                        .withDate(dateTimeService.utcNow())
                        .withMessage(e.getMessage())
                        .build();
    }
}

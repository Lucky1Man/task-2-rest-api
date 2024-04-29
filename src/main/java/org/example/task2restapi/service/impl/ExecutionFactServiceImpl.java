package org.example.task2restapi.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.task2restapi.dto.ExecutionFactFilterOptionsDto;
import org.example.task2restapi.dto.ExecutionFactUploadResultDto;
import org.example.task2restapi.dto.GetDetailedExecutionFactDto;
import org.example.task2restapi.dto.GetExecutionFactDto;
import org.example.task2restapi.dto.RecordExecutionFactDto;
import org.example.task2restapi.dto.UpdateExecutionFactDto;
import org.example.task2restapi.entity.ExecutionFact;
import org.example.task2restapi.entity.Participant;
import org.example.task2restapi.repository.ExecutionFactRepository;
import org.example.task2restapi.repository.ParticipantRepository;
import org.example.task2restapi.service.DateTimeService;
import org.example.task2restapi.service.ExecutionFactService;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ExecutionFactServiceImpl implements ExecutionFactService {

    private final ExecutionFactRepository factRepository;

    private final ParticipantRepository participantRepository;

    private final ModelMapper modelMapper;

    private final DateTimeService dateTimeService;

    @PostConstruct
    private void configureModelMapper() {
        modelMapper.emptyTypeMap(RecordExecutionFactDto.class, ExecutionFact.class).addMappings(mapping -> {
            mapping.skip(ExecutionFact::setId);
            mapping.using((MappingContext<UUID, Participant> ctx) -> {
                        UUID id = ctx.getSource();
                        if (id == null) {
                            return null;
                        } else {
                            return getRawParticipantOfThrowNorFound(id);
                        }
                    }
            ).map(RecordExecutionFactDto::getExecutorId, ExecutionFact::setExecutor);
            mapping.when(Conditions.isNull()).with(req -> dateTimeService.utcNow()).map(
                    RecordExecutionFactDto::getStartTime,
                    ExecutionFact::setStartTime
            );
        }).implicitMappings();
    }

    private Participant getRawParticipantOfThrowNorFound(UUID id) {
        return participantRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Executor with id '%s' not found".formatted(id))
        );
    }

    @Override
    public UUID recordExecutionFact(@NotNull @Valid RecordExecutionFactDto factDto) {
        if(factDto.getFinishTime() != null && factDto.getStartTime() == null) {
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
        if(factDto.getDescription() != null) {
            fact.setDescription(factDto.getDescription());
        }
        if(factDto.getExecutorId() != null) {
            fact.setExecutor(getRawParticipantOfThrowNorFound(factDto.getExecutorId()));
        }
        if(factDto.getStartTime() != null) {
            fact.setStartTime(factDto.getStartTime());
        }
        if(factDto.getFinishTime() != null) {
            fact.setFinishTime(factDto.getFinishTime());
        }
    }

    @Override
    public void deleteById(@NotNull UUID id) {
        factRepository.deleteById(id);
    }

    @Override
    public List<GetExecutionFactDto> findAll(@NotNull @Valid ExecutionFactFilterOptionsDto factFilterOptionsDto) {
        return null;
    }

    @Override
    public void generateReport(@NotNull @Valid ExecutionFactFilterOptionsDto factFilterOptionsDto, @NotNull HttpServletResponse response) {

    }

    @Override
    public ExecutionFactUploadResultDto uploadFromFile(@NotNull MultipartFile multipart) {
        return null;
    }
}

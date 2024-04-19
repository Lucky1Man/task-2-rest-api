package org.example.task2restapi.service;

import jakarta.validation.Valid;
import org.example.task2restapi.dto.GetParticipantDto;
import org.example.task2restapi.dto.RegisterParticipantDto;
import org.example.task2restapi.dto.UpdateParticipantDto;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Validated
public interface ParticipantService {
    List<GetParticipantDto> findAll();

    UUID register(@Valid RegisterParticipantDto participantDto);

    void updateParticipant(UUID id, @Valid UpdateParticipantDto participantDto);

    void deleteParticipant(UUID id);
}

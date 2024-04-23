package org.example.task2restapi.service.impl;

import org.example.task2restapi.dto.GetParticipantDto;
import org.example.task2restapi.dto.RegisterParticipantDto;
import org.example.task2restapi.dto.UpdateParticipantDto;
import org.example.task2restapi.service.ParticipantService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantServiceImpl implements ParticipantService {
    @Override
    public List<GetParticipantDto> findAll() {
        return null;
    }

    @Override
    public UUID register(RegisterParticipantDto participantDto) {
        return null;
    }

    @Override
    public void updateParticipant(UUID id, UpdateParticipantDto participantDto) {

    }

    @Override
    public void deleteParticipant(UUID id) {

    }
}

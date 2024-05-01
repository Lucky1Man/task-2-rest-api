package org.example.task2restapi.service.impl;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.task2restapi.dto.GetParticipantDto;
import org.example.task2restapi.dto.RegisterParticipantDto;
import org.example.task2restapi.dto.UpdateParticipantDto;
import org.example.task2restapi.entity.Participant;
import org.example.task2restapi.repository.ParticipantRepository;
import org.example.task2restapi.service.ParticipantService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<GetParticipantDto> findAll() {
        return participantRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, GetParticipantDto.class))
                .toList();
    }

    @Override
    public UUID register(@NotNull @Valid RegisterParticipantDto participantDto) {
        throwIfEmailTaken(participantDto.getEmail());
        return participantRepository.save(modelMapper.map(participantDto, Participant.class)).getId();
    }

    private void throwIfEmailTaken(String email) {
        participantRepository.findByEmail(email).ifPresent(ignored -> {
            throw new IllegalArgumentException("Email %s is already taken.".formatted(email));
        });
    }

    @Override
    public void updateParticipant(@NotNull UUID id, @Valid @NotNull UpdateParticipantDto participantDto) {
        Participant participant = participantRepository.findById(id).orElseThrow( () ->
                new IllegalArgumentException("Participant with id '%s' not found".formatted(id))
        );
        if(participantDto.getEmail() != null) {
            throwIfEmailTaken(participantDto.getEmail());
            participant.setEmail(participantDto.getEmail());
        }
        if(participantDto.getFullName() != null) {
            participant.setFullName(participantDto.getFullName());
        }
    }

    @Override
    public void deleteParticipant(@NotNull UUID id) {
        participantRepository.deleteById(id);
    }
}

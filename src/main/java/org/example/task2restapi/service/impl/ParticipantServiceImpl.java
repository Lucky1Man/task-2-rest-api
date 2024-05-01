package org.example.task2restapi.service.impl;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<GetParticipantDto> findAll() {
        log.debug("getting all participants");
        List<Participant> all = participantRepository.findAll();
        log.debug("got {}", all);
        List<GetParticipantDto> mapped = all.stream()
                .map(entity -> modelMapper.map(entity, GetParticipantDto.class))
                .toList();
        log.debug("mapped {}", mapped);
        return mapped;
    }

    @Override
    public UUID register(@NotNull @Valid RegisterParticipantDto participantDto) {
        throwIfEmailTaken(participantDto.getEmail());
        log.debug("mapping RegisterParticipantDto {}", participantDto);
        Participant mapped = modelMapper.map(participantDto, Participant.class);
        log.debug("mapped participant {}", mapped);
        Participant saved = participantRepository.save(mapped);
        log.debug("saved participant {}", saved);
        return saved.getId();
    }

    private void throwIfEmailTaken(String email) {
        participantRepository.findByEmail(email).ifPresent(ignored -> {
            IllegalArgumentException ex = new IllegalArgumentException("Email %s is already taken.".formatted(email));
            log.debug("throwIfEmailTaken()", ex);
            throw ex;
        });
    }

    @Override
    public void updateParticipant(@NotNull UUID id, @Valid @NotNull UpdateParticipantDto participantDto) {
        log.debug("updating participant with id {}, with data {}", id, participantDto);
        Participant participant = participantRepository.findById(id).orElseThrow( () ->
                {
                    IllegalArgumentException ex = new IllegalArgumentException("Participant with id '%s' not found".formatted(id));
                    log.debug("updateParticipant()", ex);
                    return ex;
                }
        );
        if(participantDto.getEmail() != null) {
            throwIfEmailTaken(participantDto.getEmail());
            participant.setEmail(participantDto.getEmail());
        }
        if(participantDto.getFullName() != null) {
            participant.setFullName(participantDto.getFullName());
        }
        log.debug("updated participant {}", participant);
    }

    @Override
    public void deleteParticipant(@NotNull UUID id) {
        log.debug("deleting participant with id {}", id);
        participantRepository.deleteById(id);
    }
}

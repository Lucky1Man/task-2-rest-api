package org.example.task2restapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.task2restapi.Task2RestApiApplication;
import org.example.task2restapi.config.TestDbConfig;
import org.example.task2restapi.dto.GetDetailedExecutionFactDto;
import org.example.task2restapi.dto.GetParticipantDto;
import org.example.task2restapi.dto.RecordExecutionFactDto;
import org.example.task2restapi.dto.UpdateExecutionFactDto;
import org.example.task2restapi.entity.ExecutionFact;
import org.example.task2restapi.entity.Participant;
import org.example.task2restapi.repository.ExecutionFactRepository;
import org.example.task2restapi.repository.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = {Task2RestApiApplication.class, TestDbConfig.class}
)
@AutoConfigureMockMvc
class ExecutionFactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @SpyBean
    ParticipantRepository participantRepository;

    @SpyBean
    ExecutionFactRepository executionFactRepository;

    @Autowired
    ObjectMapper objectMapper;

    List<Participant> initialParticipants = List.of(
            Participant.builder()
                    .withFullName("Test 1")
                    .withEmail("test1@gmail.com")
                    .build(),
            Participant.builder()
                    .withFullName("Test 2")
                    .withEmail("test2@gmail.com")
                    .build()
    );

    @BeforeEach
    void initDb() {
        participantRepository.deleteAll();
        executionFactRepository.deleteAll();
        initialParticipants = participantRepository.saveAll(initialParticipants);
        reset(participantRepository, executionFactRepository);
    }

    ExecutionFact saveAndGetStandardExecutionFactFromDb() {
        return saveAndGetExecutionFactFromDb(1, initialParticipants.get(0), "a");
    }

    ExecutionFact saveAndGetExecutionFactFromDb(int startMonth, Participant executor, String descriptionCharacter) {
        LocalDateTime startTime = LocalDateTime.of(2000, startMonth, 1, 0, 0);
        LocalDateTime finishTime = startTime.plusMinutes(10);
        String description = String.join("", Collections.nCopies(500, descriptionCharacter));
        ExecutionFact executionFact = ExecutionFact.builder()
                .withExecutor(executor)
                .withDescription(description)
                .withFinishTime(finishTime)
                .withStartTime(startTime)
                .build();
        return executionFactRepository.saveAndFlush(executionFact);
    }

    @SneakyThrows
    @Test
    void recordExecutionFact_shouldSaveGivenDataAndReturnItsID_ifItIsValid() {
        //given
        LocalDateTime startTime = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime finishTime = startTime.plusMinutes(10);
        UUID participantId = initialParticipants.get(0).getId();
        String description = String.join("", Collections.nCopies(500, "a"));
        RecordExecutionFactDto executionFactDto = new RecordExecutionFactDto(
                participantId, description, startTime, finishTime
        );
        //when
        String result = mockMvc.perform(
                        post("/api/v1/execution-facts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(executionFactDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        //then
        UUID id = assertDoesNotThrow(() -> UUID.fromString(result), "Returned id should be valid.");
        ExecutionFact fromDb = executionFactRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Given fact was not saved."));
        assertEquals(executionFactDto.getExecutorId(), fromDb.getExecutor().getId());
        assertEquals(executionFactDto.getDescription(), fromDb.getDescription());
        assertEquals(executionFactDto.getStartTime(), fromDb.getStartTime());
        assertEquals(executionFactDto.getFinishTime(), fromDb.getFinishTime());
    }

    @SneakyThrows
    @Test
    void recordExecutionFact_shouldReturnResponseContainingAllValidationErrorsAndShouldNotSaveInvalidData() {
        //given
        LocalDateTime startTime = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime finishTime = startTime.minusMinutes(10);
        UUID participantId = initialParticipants.get(0).getId();
        String description = String.join("", Collections.nCopies(501, "a"));
        RecordExecutionFactDto executionFactDto = new RecordExecutionFactDto(
                participantId, description, startTime, finishTime
        );
        //when
        String result = mockMvc.perform(
                        post("/api/v1/execution-facts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(executionFactDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        //then
        ExceptionResponse exceptions = assertDoesNotThrow(() -> objectMapper.readValue(result, ExceptionResponse.class), "It should return exceptions");
        assertTrue(exceptions.getMessage().contains("recordExecutionFact.factDto: startTime is after finishTime"),
                "Should contain date time range validation message.");
        assertTrue(exceptions.getMessage().contains("recordExecutionFact.factDto.description: length must be between 1 and 500"),
                "Should contain description length validation message.");
        assertTrue(executionFactRepository.findAll().isEmpty(), "Execution facts table should be empty");
    }

    @Test
    @SneakyThrows
    void recordExecutionFact_shouldReturnExceptionResponse_ifGivenExecutorIdDoesNotExist() {
        //given
        LocalDateTime startTime = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime finishTime = startTime.plusMinutes(10);
        UUID participantId = initialParticipants.get(0).getId();
        String description = String.join("", Collections.nCopies(500, "a"));
        RecordExecutionFactDto executionFactDto = new RecordExecutionFactDto(
                participantId, description, startTime, finishTime
        );
        participantRepository.deleteAll();
        participantRepository.flush();
        //when
        String result = mockMvc.perform(
                        post("/api/v1/execution-facts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(executionFactDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        //then
        ExceptionResponse exceptions = assertDoesNotThrow(() -> objectMapper.readValue(result, ExceptionResponse.class), "It should return exceptions");
        assertTrue(exceptions.getMessage().contains("Executor with id '%s' not found".formatted(participantId)),
                "Should contain executor not found message.");
        assertTrue(executionFactRepository.findAll().isEmpty(), "Execution facts table should be empty");
    }

    @SneakyThrows
    @Test
    void getById_shouldReturnDetailedExecutionFactDto_ifThereIsAnyWithGivenId() {
        //given
        ExecutionFact expectedExecutionFact = saveAndGetStandardExecutionFactFromDb();
        //when
        String result = mockMvc.perform(
                        get("/api/v1/execution-facts/" + expectedExecutionFact.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        //then
        GetDetailedExecutionFactDto dto = assertDoesNotThrow(
                () -> objectMapper.readValue(result, GetDetailedExecutionFactDto.class),
                "It should return valid get dto"
        );
        assertEquals(expectedExecutionFact.getDescription(), dto.getDescription());
        assertEquals(expectedExecutionFact.getStartTime(), dto.getStartTime());
        assertEquals(expectedExecutionFact.getFinishTime(), dto.getFinishTime());
        assertEquals(expectedExecutionFact.getId(), dto.getId());
        Participant expectedExecutor = expectedExecutionFact.getExecutor();
        GetParticipantDto getParticipantDto = dto.getExecutor();
        assertEquals(expectedExecutor.getEmail(), getParticipantDto.getEmail());
        assertEquals(expectedExecutor.getFullName(), getParticipantDto.getFullName());
    }

    @Test
    @SneakyThrows
    void getById_shouldReturnExceptionResponse_ifExecutorFactWithGivenIdDoesNotExist() {
        //given
        UUID factId = UUID.randomUUID();
        //when
        String result = mockMvc.perform(
                        get("/api/v1/execution-facts/" + factId))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        //then
        ExceptionResponse exceptions = assertDoesNotThrow(() -> objectMapper.readValue(result, ExceptionResponse.class), "It should return exceptions");
        assertTrue(exceptions.getMessage().contains("Execution fact with id '%s' not found".formatted(factId)),
                "Should contain execution fact not found message.");
    }

    @Test
    @SneakyThrows
    void updateExecutionFact_shouldUpdateExecutionFactWithAllNewData() {
        //given
        ExecutionFact saved = saveAndGetStandardExecutionFactFromDb();
        LocalDateTime newStartTime = LocalDateTime.of(2000, 2, 1, 0, 0);
        LocalDateTime newFinishTime = newStartTime.plusMinutes(10);
        UUID newExecutorId = initialParticipants.get(1).getId();
        String newDescription = String.join("", Collections.nCopies(500, "b"));
        UpdateExecutionFactDto updateDto = new UpdateExecutionFactDto(
                newStartTime, newFinishTime, newDescription, newExecutorId
        );
        //when
        mockMvc.perform(
                        put("/api/v1/execution-facts/" + saved.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
        //then
        ExecutionFact updated = executionFactRepository.findById(saved.getId()).get();
        assertEquals(updateDto.getStartTime(), updated.getStartTime());
        assertEquals(updateDto.getFinishTime(), updated.getFinishTime());
        assertEquals(updateDto.getDescription(), updated.getDescription());
        assertEquals(updateDto.getExecutorId(), updated.getExecutor().getId());
    }

    @Test
    @SneakyThrows
    void updateExecutionFact_shouldModifyOnlyGivenData() {
        //given
        ExecutionFact saved = saveAndGetStandardExecutionFactFromDb();
        UUID newExecutorId = initialParticipants.get(1).getId();
        String newDescription = String.join("", Collections.nCopies(500, "b"));
        UpdateExecutionFactDto updateDto = new UpdateExecutionFactDto(
                null, null, newDescription, newExecutorId
        );
        //when
        mockMvc.perform(
                        put("/api/v1/execution-facts/" + saved.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
        //then
        ExecutionFact updated = executionFactRepository.findById(saved.getId()).get();
        assertEquals(saved.getStartTime(), updated.getStartTime());
        assertEquals(saved.getFinishTime(), updated.getFinishTime());
        assertEquals(updateDto.getDescription(), updated.getDescription());
        assertEquals(updateDto.getExecutorId(), updated.getExecutor().getId());
    }

    @Test
    @SneakyThrows
    void updateExecutionFact_shouldReturnExceptionResponse_ifGivenUpdateDtoIsNotValid() {
        //given
        ExecutionFact saved = saveAndGetStandardExecutionFactFromDb();
        LocalDateTime newStartTime = LocalDateTime.of(2000, 2, 1, 0, 0);
        LocalDateTime newFinishTime = newStartTime.minusMinutes(10);
        UUID newExecutorId = initialParticipants.get(1).getId();
        String newDescription = String.join("", Collections.nCopies(501, "b"));
        UpdateExecutionFactDto updateDto = new UpdateExecutionFactDto(
                newStartTime, newFinishTime, newDescription, newExecutorId
        );
        //when
        String result = mockMvc.perform(
                        put("/api/v1/execution-facts/" + saved.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        //then
        ExceptionResponse exceptions = assertDoesNotThrow(() -> objectMapper.readValue(result, ExceptionResponse.class), "It should return exceptions");
        assertTrue(exceptions.getMessage().contains("updateExecutionFact.factDto: startTime is after finishTime"),
                "Should contain date time range validation message.");
        assertTrue(exceptions.getMessage().contains("updateExecutionFact.factDto.description: length must be between 1 and 500"),
                "Should contain description length validation message.");
        ExecutionFact updated = executionFactRepository.findById(saved.getId()).get();
        assertEquals(saved.getStartTime(), updated.getStartTime());
        assertEquals(saved.getFinishTime(), updated.getFinishTime());
        assertEquals(saved.getDescription(), updated.getDescription());
        assertEquals(saved.getExecutor().getId(), updated.getExecutor().getId());
    }

    @Test
    @SneakyThrows
    void updateExecutionFact_shouldReturnExceptionResponse_ifGivenNewExecutorIdWasNotfound() {
        //given
        ExecutionFact saved = saveAndGetStandardExecutionFactFromDb();
        UUID newExecutorId = UUID.randomUUID();
        UpdateExecutionFactDto updateDto = new UpdateExecutionFactDto(
                null, null, null, newExecutorId
        );
        //when
        String result = mockMvc.perform(
                        put("/api/v1/execution-facts/" + saved.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        //then
        ExceptionResponse exceptions = assertDoesNotThrow(() -> objectMapper.readValue(result, ExceptionResponse.class), "It should return exceptions");
        assertTrue(exceptions.getMessage().contains("Executor with id '%s' not found".formatted(newExecutorId)),
                "Should contain executor not found message.");
        ExecutionFact updated = executionFactRepository.findById(saved.getId()).get();
        assertEquals(saved.getStartTime(), updated.getStartTime());
        assertEquals(saved.getFinishTime(), updated.getFinishTime());
        assertEquals(saved.getDescription(), updated.getDescription());
        assertEquals(saved.getExecutor().getId(), updated.getExecutor().getId());
    }

    @Test
    @SneakyThrows
    void updateExecutionFact_shouldReturnExceptionResponse_ifGivenExecutionFactIdWasNotfound() {
        //given
        ExecutionFact saved = saveAndGetStandardExecutionFactFromDb();
        LocalDateTime newStartTime = LocalDateTime.of(2000, 2, 1, 0, 0);
        LocalDateTime newFinishTime = newStartTime.plusMinutes(10);
        UUID newExecutorId = initialParticipants.get(1).getId();
        String newDescription = String.join("", Collections.nCopies(500, "b"));
        UpdateExecutionFactDto updateDto = new UpdateExecutionFactDto(
                newStartTime, newFinishTime, newDescription, newExecutorId
        );
        UUID factId = UUID.randomUUID();
        //when
        String result = mockMvc.perform(
                        put("/api/v1/execution-facts/" + factId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        //then
        ExceptionResponse exceptions = assertDoesNotThrow(() -> objectMapper.readValue(result, ExceptionResponse.class), "It should return exceptions");
        assertTrue(exceptions.getMessage().contains("Execution fact with id '%s' not found".formatted(factId)),
                "Should contain execution fact not found message.");
        ExecutionFact updated = executionFactRepository.findById(saved.getId()).get();
        assertEquals(saved.getStartTime(), updated.getStartTime());
        assertEquals(saved.getFinishTime(), updated.getFinishTime());
        assertEquals(saved.getDescription(), updated.getDescription());
        assertEquals(saved.getExecutor().getId(), updated.getExecutor().getId());
    }

    @Test
    @SneakyThrows
    void deleteExecutionFact_shouldDeleteExecutionFactWithGivenId() {
        //given
        ExecutionFact saved = saveAndGetStandardExecutionFactFromDb();
        ExecutionFact expectedToBePresent = saveAndGetExecutionFactFromDb(3, initialParticipants.get(0), "n");
        //when
        mockMvc.perform(
                        delete("/api/v1/execution-facts/" + saved.getId()))
                .andExpect(status().isOk());
        //then
        assertTrue(executionFactRepository.findById(saved.getId()).isEmpty(), "Db should not contain deleted fact");
        assertTrue(executionFactRepository.findById(expectedToBePresent.getId()).isPresent(), "It should not affect other records in db");
    }

    @Test
    @SneakyThrows
    void deleteExecutionFact_shouldReturnExceptionResponse_ifGivenFactIdDoesNotExist() {
        //given
        ExecutionFact saved = saveAndGetStandardExecutionFactFromDb();
        UUID factId = UUID.randomUUID();
        //when
        String result = mockMvc.perform(
                        delete("/api/v1/execution-facts/" + factId))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        ExceptionResponse exceptions = assertDoesNotThrow(() -> objectMapper.readValue(result, ExceptionResponse.class), "It should return exceptions");
        assertTrue(exceptions.getMessage().contains("Execution fact with id '%s' not found".formatted(factId)),
                "Should contain execution fact not found message.");
        assertTrue(executionFactRepository.findById(saved.getId()).isPresent(), "It should not affect other records in db");
    }


}

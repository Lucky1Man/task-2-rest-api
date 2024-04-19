package org.example.task2restapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.example.task2restapi.dto.GetParticipantDto;
import org.example.task2restapi.dto.RegisterParticipantDto;
import org.example.task2restapi.dto.UpdateParticipantDto;
import org.example.task2restapi.service.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/participants")
public class ParticipantController {

    private final ParticipantService participantService;

    @GetMapping
    @Operation(
            description = "Returns list of registered participants"
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = GetParticipantDto.class)
                    )
            )
    )
    public List<GetParticipantDto> getParticipants() {
        return participantService.findAll();
    }

    @PostMapping
    @Operation(
            description = "Register participant."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Participant was successfully registered. It returns id of created participant",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UUID.class)
            )
    )
    public ResponseEntity<UUID> register(@RequestBody RegisterParticipantDto participantDto) {
        return ResponseEntity.status(HttpStatus.CREATED.value())
                .body(participantService.register(participantDto));
    }

    @PutMapping("/{id}")
    @Operation(
            description = "Updates participant with given id with data from UpdateParticipantDto." +
                    " If UpdateParticipantDto has null fields then that specific field will be ignored"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Means that execution fact was updated and all given parameters were changed"
    )
    public void updateParticipant(@PathVariable UUID id, @RequestBody UpdateParticipantDto participantDto) {
        participantService.updateParticipant(id, participantDto);
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Deletes participant by specified id. Returns 404 if no participant was found")
    @ApiResponse(
            responseCode = "200",
            description = "Returns participant with specified id or email",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GetParticipantDto.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Participant with specified id was not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class)
            )
    )
    public void deleteParticipant(@PathVariable UUID id) {
        participantService.deleteParticipant(id);
    }

}

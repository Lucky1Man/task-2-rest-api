package org.example.task2restapi.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.task2restapi.dto.ExecutionFactFilterOptionsDto;
import org.example.task2restapi.dto.ExecutionFactUploadResultDto;
import org.example.task2restapi.dto.GetDetailedExecutionFactDto;
import org.example.task2restapi.dto.GetExecutionFactDto;
import org.example.task2restapi.dto.RecordExecutionFactDto;
import org.example.task2restapi.dto.UpdateExecutionFactDto;
import org.example.task2restapi.service.ExecutionFactService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/execution-facts")
public class ExecutionFactController {

    private final ExecutionFactService factService;

    @PostMapping
    @Operation(
            description = "Records given execution fact."
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(
            responseCode = "201",
            description = "Returns id of execution fact that was recorded.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UUID.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Returns message containing all validation errors.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class)
            )
    )
    public ResponseEntity<UUID> recordExecutionFact(@RequestBody RecordExecutionFactDto factDto) {
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(factService.recordExecutionFact(factDto));
    }

    @GetMapping("/{id}")
    @Operation(description = "Returns detailed version of execution fact by given id.")
    @ApiResponse(
            responseCode = "200",
            description = "Retrieved",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GetExecutionFactDto.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Means that execution fact with given id was not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class)
            )
    )
    public GetDetailedExecutionFactDto getById(@PathVariable UUID id) {
        return factService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(
            description = "Updates execution fact with given id with data from UpdateExecutionFactDto." +
                    " If UpdateExecutionFactDto has null fields then that specific field will be ignored"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Means that execution fact was updated and all given parameters were changed"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Given data is invalid.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class)
            )
    )
    public void updateExecutionFact(@PathVariable UUID id, @RequestBody UpdateExecutionFactDto factDto) {
        factService.updateExecutionFact(id, factDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            description = "Deletes execution fact with given id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Means that execution fact was deleted"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Means that execution fact with given id does not exist",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class)
            )
    )
    public void deleteExecutionFact(@PathVariable UUID id) {
        factService.deleteById(id);
    }

    @PostMapping(path = "/_list")
    @Operation(description = "Returns execution facts based on given filter")
    @ApiResponse(
            responseCode = "200",
            description = "Retrieved",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = GetExecutionFactDto.class)
                    )
            )
    )
    public List<GetExecutionFactDto> getFiltered(@RequestBody ExecutionFactFilterOptionsDto factFilterOptionsDto) {
        return factService.findAll(factFilterOptionsDto);
    }

    @PostMapping(value = "/_report", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(description = "Generates file containing all execution facts specified by filter")
    @ApiResponse(
            responseCode = "200",
            description = "Retrieved",
            content = @Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE
            )
    )
    public void generateReport(@RequestBody ExecutionFactFilterOptionsDto factFilterOptionsDto, HttpServletResponse response) {
        factService.generateReport(factFilterOptionsDto, response);
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Writes all valid execution facts from given json file to database." +
            " It returns count of added execution facts and list of declined objects and info what is wrong with them.")
    @ApiResponse(
            responseCode = "201",
            description = "Valid facts where saved.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExecutionFactUploadResultDto.class)
            )
    )
    public ExecutionFactUploadResultDto uploadFromFile(@RequestParam("file") MultipartFile multipart) {
        return factService.uploadFromFile(multipart);
    }

}

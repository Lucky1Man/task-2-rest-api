package org.example.task2restapi.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateParticipantDto {

    @Length(min = 1, max = 100)
    @Nullable
    private String fullName;

    @Length(min = 3, max = 320)
    @Nullable
    private String email;

    @Nullable
    @Valid
    private ChangePasswordDto changePasswordRequest;

}

package org.example.task2restapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.task2restapi.validator.PasswordFormat;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterParticipantDto {

    @Length(min = 1, max = 100)
    @NotNull(message = "Participant must have full name")
    private String fullName;

    @Length(min = 3, max = 320)
    @NotNull(message = "Participant must have email")
    @Email
    private String email;

    @Length(min = 8, max = 72)
    @NotNull(message = "Participant must have password")
    @PasswordFormat
    private String password;

}

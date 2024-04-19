package org.example.task2restapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.task2restapi.validator.PasswordFormat;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {
    @NotNull(message = "You must provide old password.")
    @Length(min = 8, max = 72)
    @PasswordFormat
    private String oldPassword;

    @NotNull(message = "You must provide new password")
    @Length(min = 8, max = 72)
    @PasswordFormat
    private String newPassword;
}

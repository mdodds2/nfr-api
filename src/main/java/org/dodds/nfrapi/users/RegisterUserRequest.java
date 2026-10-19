package org.dodds.nfrapi.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {

    @NotBlank(message = "Name is required")
    @Size(min=5, max = 255, message="Name must be a minimum of 5 and a maximum of 255 characters")
    private String name;

    @NotBlank
    @Email(message="Must be a valid email")
    private String email;

    @NotBlank
    @Size(min=8, max=72, message = "Password must be between 8 to 72 characters long")
    private String password;
}

package com.v1.manfaa.DTO.In;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTOIn {

    @NotBlank(message = "username is required")
    @Size(min = 3, max = 50, message = "username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 8, message = "password must be at least 8 characters")
    private String password;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @NotBlank(message = "full name is required")
    @Size(max = 100, message = "full name must be at most 100 characters")
    private String fullName;

    @NotBlank(message = "phone number is required")
    @Pattern(
            regexp = "^[0-9]{8,15}$",
            message = "phone number must contain only digits"
    )
    private String phoneNumber;

    @NotBlank(message = "company name is required")
    @Size(max = 20, message = "company name must be at most 20 characters")
    private String companyName;

    @NotBlank(message = "industry is required")
    @Size(max = 20, message = "industry must be at most 20 characters")
    private String industry;

    @NotNull(message = "team size is required")
    @Min(value = 1, message = "team size must be at least 1")
    private Integer teamSize;

    @NotBlank(message = "description is required")
    @Size(max = 1000, message = "description must be at most 1000 characters")
    private String description;
}


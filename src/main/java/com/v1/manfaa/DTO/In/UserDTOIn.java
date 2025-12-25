package com.v1.manfaa.DTO.In;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTOIn {

    @NotEmpty(message = "Username should not be empty")
    private String username;

    @NotEmpty(message = "Username should not be empty")
    private String fullName;

    @NotEmpty(message = "password should not be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty
    @Pattern( regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "Password must have at least 8 characters, " +
                    "and less than 20 characters, " +
                    "one uppercase letter, one lowercase letter, one number, " +
                    "one special character, one digit!")
    private String password;



    @NotEmpty
    @Pattern(regexp = "^9665\\d{8}$",
            message = "Phone number must be in the format 9665xxxxxxxx")
    private String phone_Number;


}

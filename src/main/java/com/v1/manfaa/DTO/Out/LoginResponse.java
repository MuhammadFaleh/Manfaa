package com.v1.manfaa.DTO.Out;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private String message;
}

package com.v1.manfaa.Controller;

import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.LoginRequest;
import com.v1.manfaa.DTO.Out.LoginResponse;
import com.v1.manfaa.Service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(loginRequest, response);
        return ResponseEntity.status(200).body(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.status(200).body(new ApiResponse("logout successful"));
    }
}
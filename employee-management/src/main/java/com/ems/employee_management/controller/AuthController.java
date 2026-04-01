package com.ems.employee_management.controller;

import com.ems.employee_management.dto.ApiResponse;
import com.ems.employee_management.dto.LoginRequest;
import com.ems.employee_management.dto.SignupRequest;
import com.ems.employee_management.entity.User;
import com.ems.employee_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "Sign up", description = "Register new user")
    public User signup(@Valid @RequestBody SignupRequest request) {
        return userService.signup(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Get JWT token")
    public ApiResponse<String> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Login successful")
                .data(token)
                .build();
    }
}

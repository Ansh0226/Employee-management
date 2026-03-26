package com.ems.employee_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.ems.employee_management.entity.enums.Role;

@Data
public class LoginRequest {

    @NotBlank
    private String identifier; // Can be username or email and employeeId

    @NotBlank
    private String password;

    private Role role;
}

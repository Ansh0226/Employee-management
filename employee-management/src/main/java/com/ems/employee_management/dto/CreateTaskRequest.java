package com.ems.employee_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTaskRequest {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Long projectId;

    @NotNull
    private Long employeeId;
}

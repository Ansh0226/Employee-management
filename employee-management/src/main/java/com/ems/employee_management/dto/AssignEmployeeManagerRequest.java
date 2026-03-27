package com.ems.employee_management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignEmployeeManagerRequest {
    @NotNull
    private Long employeeId;

    @NotNull
    private Long managerId;
}

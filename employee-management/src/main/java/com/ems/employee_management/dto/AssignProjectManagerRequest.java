package com.ems.employee_management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignProjectManagerRequest {
    @NotNull
    private Long projectId;

    @NotNull
    private Long managerId;
}

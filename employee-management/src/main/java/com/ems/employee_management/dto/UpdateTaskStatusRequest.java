package com.ems.employee_management.dto;

import com.ems.employee_management.entity.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskStatusRequest {
    @NotNull
    private TaskStatus status;
}

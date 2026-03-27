package com.ems.employee_management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private Long projectId;
    private String projectName;
    private Long managerId;
    private String managerName;
    private Long employeeId;
    private String employeeName;
}

package com.ems.employee_management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private Long managerId;
    private String managerName;
}

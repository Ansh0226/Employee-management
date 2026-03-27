package com.ems.employee_management.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private Long managerId;
    private String managerName;
    private List<UserResponse> teamMembers;
}

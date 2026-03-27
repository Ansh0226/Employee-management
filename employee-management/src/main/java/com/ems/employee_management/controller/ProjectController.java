package com.ems.employee_management.controller;

import com.ems.employee_management.dto.ApiResponse;
import com.ems.employee_management.dto.AssignProjectManagerRequest;
import com.ems.employee_management.dto.CreateProjectRequest;
import com.ems.employee_management.dto.ProjectDetailResponse;
import com.ems.employee_management.dto.ProjectResponse;
import com.ems.employee_management.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ApiResponse<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        return ApiResponse.<ProjectResponse>builder()
                .success(true)
                .message("Project created successfully")
                .data(projectService.toResponse(projectService.createProject(request)))
                .build();
    }

    @PutMapping("/assign-manager")
    public ApiResponse<ProjectResponse> assignManager(@Valid @RequestBody AssignProjectManagerRequest request) {
        return ApiResponse.<ProjectResponse>builder()
                .success(true)
                .message("Project assigned successfully")
                .data(projectService.toResponse(projectService.assignManager(request)))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ProjectResponse>> getAllProjects() {
        return ApiResponse.<List<ProjectResponse>>builder()
                .success(true)
                .message("Projects fetched successfully")
                .data(projectService.getAllProjects().stream().map(projectService::toResponse).toList())
                .build();
    }

    @GetMapping("/manager/{managerId}")
    public ApiResponse<List<ProjectResponse>> getProjectsForManager(@PathVariable Long managerId) {
        return ApiResponse.<List<ProjectResponse>>builder()
                .success(true)
                .message("Manager projects fetched successfully")
                .data(projectService.getProjectsForManager(managerId).stream().map(projectService::toResponse).toList())
                .build();
    }

    @GetMapping("/my")
    public ApiResponse<List<ProjectResponse>> getMyProjects() {
        return ApiResponse.<List<ProjectResponse>>builder()
                .success(true)
                .message("My projects fetched successfully")
                .data(projectService.getMyProjects().stream().map(projectService::toResponse).toList())
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<ProjectResponse>> getProjectsForUser(@PathVariable Long userId) {
        return ApiResponse.<List<ProjectResponse>>builder()
                .success(true)
                .message("User projects fetched successfully")
                .data(projectService.getProjectsForUser(userId).stream().map(projectService::toResponse).toList())
                .build();
    }

    @GetMapping("/profile")
    public ApiResponse<List<ProjectResponse>> getProfileProjects() {
        return ApiResponse.<List<ProjectResponse>>builder()
                .success(true)
                .message("Profile projects fetched successfully")
                .data(projectService.getProfileProjects().stream().map(projectService::toResponse).toList())
                .build();
    }

    @GetMapping("/{projectId}/detail")
    public ApiResponse<ProjectDetailResponse> getProjectDetail(@PathVariable Long projectId) {
        return ApiResponse.<ProjectDetailResponse>builder()
                .success(true)
                .message("Project details fetched successfully")
                .data(projectService.toDetailResponse(projectService.getProject(projectId)))
                .build();
    }
}

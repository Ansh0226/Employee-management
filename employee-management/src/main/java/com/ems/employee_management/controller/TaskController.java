package com.ems.employee_management.controller;

import com.ems.employee_management.dto.ApiResponse;
import com.ems.employee_management.dto.CreateTaskRequest;
import com.ems.employee_management.dto.TaskResponse;
import com.ems.employee_management.dto.UpdateTaskStatusRequest;
import com.ems.employee_management.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ApiResponse<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task created successfully")
                .data(taskService.toResponse(taskService.createTask(request)))
                .build();
    }

    @GetMapping("/manager")
    public ApiResponse<List<TaskResponse>> getManagerTasks() {
        return ApiResponse.<List<TaskResponse>>builder()
                .success(true)
                .message("Manager tasks fetched successfully")
                .data(taskService.getManagerTasks().stream().map(taskService::toResponse).toList())
                .build();
    }

    @GetMapping("/employee")
    public ApiResponse<List<TaskResponse>> getEmployeeTasks() {
        return ApiResponse.<List<TaskResponse>>builder()
                .success(true)
                .message("Employee tasks fetched successfully")
                .data(taskService.getEmployeeTasks().stream().map(taskService::toResponse).toList())
                .build();
    }

    @GetMapping("/pending-approvals")
    public ApiResponse<List<TaskResponse>> getPendingApprovals() {
        return ApiResponse.<List<TaskResponse>>builder()
                .success(true)
                .message("Pending task approvals fetched successfully")
                .data(taskService.getPendingApprovals().stream().map(taskService::toResponse).toList())
                .build();
    }

    @PutMapping("/employee/{taskId}/status")
    public ApiResponse<TaskResponse> updateEmployeeTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {

        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task updated successfully")
                .data(taskService.toResponse(taskService.updateEmployeeTaskStatus(taskId, request)))
                .build();
    }

    @PutMapping("/manager/{taskId}/approve")
    public ApiResponse<TaskResponse> approveTask(@PathVariable Long taskId) {
        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task approved successfully")
                .data(taskService.toResponse(taskService.approveTask(taskId)))
                .build();
    }
}

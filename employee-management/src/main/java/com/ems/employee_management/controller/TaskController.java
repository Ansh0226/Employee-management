package com.ems.employee_management.controller;

import com.ems.employee_management.dto.ApiResponse;
import com.ems.employee_management.dto.CreateTaskRequest;
import com.ems.employee_management.dto.TaskResponse;
import com.ems.employee_management.dto.UpdateTaskStatusRequest;
import com.ems.employee_management.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Task", description = "Task actions")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create task", description = "Assign task to employee")
    public ApiResponse<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task created successfully")
                .data(taskService.toResponse(taskService.createTask(request)))
                .build();
    }

    @GetMapping("/manager")
    @Operation(summary = "Manager tasks", description = "Show manager tasks")
    public ApiResponse<List<TaskResponse>> getManagerTasks() {
        return ApiResponse.<List<TaskResponse>>builder()
                .success(true)
                .message("Manager tasks fetched successfully")
                .data(taskService.getManagerTasks().stream().map(taskService::toResponse).toList())
                .build();
    }

    @GetMapping("/employee")
    @Operation(summary = "Employee tasks", description = "Show employee tasks")
    public ApiResponse<List<TaskResponse>> getEmployeeTasks() {
        return ApiResponse.<List<TaskResponse>>builder()
                .success(true)
                .message("Employee tasks fetched successfully")
                .data(taskService.getEmployeeTasks().stream().map(taskService::toResponse).toList())
                .build();
    }

    @PutMapping("/employee/{taskId}/status")
    @Operation(summary = "Complete task", description = "Mark task completed")
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
    @Operation(summary = "Approve task", description = "Approve completed task")
    public ApiResponse<TaskResponse> approveTask(@PathVariable Long taskId) {
        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task approved successfully")
                .data(taskService.toResponse(taskService.approveTask(taskId)))
                .build();
    }

    @DeleteMapping("/manager/{taskId}")
    @Operation(summary = "Delete task", description = "Delete manager task")
    public ApiResponse<String> deleteTask(@PathVariable Long taskId) {
        taskService.deleteManagerTask(taskId);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Task deleted successfully")
                .data("Deleted successfully")
                .build();
    }
}

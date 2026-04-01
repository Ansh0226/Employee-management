package com.ems.employee_management.controller;

import com.ems.employee_management.dto.ApiResponse;
import com.ems.employee_management.dto.AssignEmployeeManagerRequest;
import com.ems.employee_management.dto.UserResponse;
import com.ems.employee_management.entity.User;
import com.ems.employee_management.entity.enums.Role;
import com.ems.employee_management.service.AdminService;
import com.ems.employee_management.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin actions")
public class AdminController {

    private final AdminService adminService;
    private final EmployeeService employeeService;

    @GetMapping("/users")
    @Operation(summary = "List users", description = "Show all users")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = adminService.getAllUsers()
                .stream()
                .map(employeeService::mapToDto)
                .toList();

        return ApiResponse.<List<UserResponse>>builder()
                .success(true)
                .message("All users fetched successfully")
                .data(users)
                .build();
    }

    @PutMapping("/approve/{id}")
    @Operation(summary = "Approve user", description = "Approve pending user")
    public ApiResponse<UserResponse> approveUser(@PathVariable Long id) {
        User user = adminService.approveUser(id);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User approved successfully")
                .data(employeeService.mapToDto(user))
                .build();
    }

    @PutMapping("/change-role/{id}")
    @Operation(summary = "Change role", description = "Update user role")
    public ApiResponse<UserResponse> changeRole(@PathVariable Long id, @RequestParam Role role) {
        User user = adminService.changeRole(id, role);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User role updated successfully")
                .data(employeeService.mapToDto(user))
                .build();
    }

    @PutMapping("/assign-manager")
    @Operation(summary = "Assign manager", description = "Map employee to manager")
    public ApiResponse<UserResponse> assignManager(@Valid @RequestBody AssignEmployeeManagerRequest request) {
        User user = adminService.assignEmployeeToManager(request.getEmployeeId(), request.getManagerId());

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Employee assigned to manager successfully")
                .data(employeeService.mapToDto(user))
                .build();
    }

    @GetMapping("/managers")
    @Operation(summary = "List managers", description = "Show approved managers")
    public ApiResponse<List<UserResponse>> getManagers() {
        List<UserResponse> users = adminService.getManagers().stream()
                .map(employeeService::mapToDto)
                .toList();

        return ApiResponse.<List<UserResponse>>builder()
                .success(true)
                .message("Managers fetched successfully")
                .data(users)
                .build();
    }

    @GetMapping("/manager-team/{managerId}")
    @Operation(summary = "Manager team", description = "Show manager team")
    public ApiResponse<List<UserResponse>> getManagerTeam(@PathVariable Long managerId) {
        List<UserResponse> users = adminService.getManagerTeam(managerId).stream()
                .map(employeeService::mapToDto)
                .toList();

        return ApiResponse.<List<UserResponse>>builder()
                .success(true)
                .message("Manager team fetched successfully")
                .data(users)
                .build();
    }
}

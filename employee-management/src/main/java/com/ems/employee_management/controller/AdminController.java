package com.ems.employee_management.controller;

import com.ems.employee_management.dto.ApiResponse;
import com.ems.employee_management.dto.UserResponse;
import com.ems.employee_management.entity.User;
import com.ems.employee_management.entity.enums.Role;
import com.ems.employee_management.service.AdminService;
import com.ems.employee_management.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final EmployeeService employeeService;

    // ✅ Get all users
    @GetMapping("/users")
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

    // ✅ Approve user
    @PutMapping("/approve/{id}")
    public ApiResponse<UserResponse> approveUser(@PathVariable Long id) {

        User user = adminService.approveUser(id);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User approved successfully")
                .data(employeeService.mapToDto(user))
                .build();
    }

    // ✅ Change role
    @PutMapping("/change-role/{id}")
    public ApiResponse<UserResponse> changeRole(
            @PathVariable Long id,
            @RequestParam Role role) {

        User user = adminService.changeRole(id, role);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User role updated successfully")
                .data(employeeService.mapToDto(user))
                .build();
    }
}
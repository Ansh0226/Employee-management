package com.ems.employee_management.controller;

import com.ems.employee_management.dto.ApiResponse;
import com.ems.employee_management.dto.UserResponse;
import com.ems.employee_management.entity.User;
import com.ems.employee_management.service.EmployeeService;
import com.ems.employee_management.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;
    private final EmployeeService employeeService;

    // ✅ Get approved employees
    @GetMapping("/employees")
    public ApiResponse<Page<UserResponse>> getApprovedEmployees(
            @RequestParam int page,
            @RequestParam int size) {

        Page<UserResponse> users = managerService
                .getApprovedEmployees(page, size)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Approved employees fetched")
                .data(users)
                .build();
    }

    // ✅ Search employees
    @GetMapping("/search")
    public ApiResponse<Page<UserResponse>> search(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int size) {

        Page<UserResponse> users = managerService
                .searchApproved(keyword, page, size)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Search results")
                .data(users)
                .build();
    }

    // ✅ Filter by location
    @GetMapping("/filter/location")
    public ApiResponse<Page<UserResponse>> filterByLocation(
            @RequestParam String location,
            @RequestParam int page,
            @RequestParam int size) {

        Page<UserResponse> users = managerService
                .filterByLocation(location, page, size)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Filtered by location")
                .data(users)
                .build();
    }

    // ✅ Update location
    @PutMapping("/update-location/{id}")
    public ApiResponse<UserResponse> updateLocation(
            @PathVariable Long id,
            @RequestParam String location) {

        User user = managerService.updateLocation(id, location);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Location updated")
                .data(employeeService.mapToDto(user))
                .build();
    }
}
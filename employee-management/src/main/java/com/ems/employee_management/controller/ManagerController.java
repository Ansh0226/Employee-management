package com.ems.employee_management.controller;

import com.ems.employee_management.dto.ApiResponse;
import com.ems.employee_management.dto.UserResponse;
import com.ems.employee_management.entity.User;
import com.ems.employee_management.entity.enums.Role;
import com.ems.employee_management.service.EmployeeService;
import com.ems.employee_management.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
@Tag(name = "Manager", description = "Manager actions")
public class ManagerController {

    private final ManagerService managerService;
    private final EmployeeService employeeService;

    @GetMapping("/employees")
    @Operation(summary = "Team directory", description = "List approved employees")
    public ApiResponse<Page<UserResponse>> getApprovedEmployees(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<UserResponse> users = managerService
                .getApprovedEmployees(page, size, role, sortBy, direction)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Approved employees fetched")
                .data(users)
                .build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search team", description = "Search team members")
    public ApiResponse<Page<UserResponse>> search(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<UserResponse> users = managerService
                .searchApproved(keyword, page, size, role, sortBy, direction)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Search results")
                .data(users)
                .build();
    }

    @GetMapping("/filter/location")
    @Operation(summary = "Filter location", description = "Filter team by location")
    public ApiResponse<Page<UserResponse>> filterByLocation(
            @RequestParam String location,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<UserResponse> users = managerService
                .filterByLocation(location, page, size, role, sortBy, direction)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Filtered by location")
                .data(users)
                .build();
    }

    @GetMapping("/filter/age-range")
    @Operation(summary = "Filter age", description = "Filter team by age")
    public ApiResponse<Page<UserResponse>> filterByAgeRange(
            @RequestParam int minAge,
            @RequestParam int maxAge,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<UserResponse> users = managerService
                .filterByAgeRange(minAge, maxAge, page, size, role, sortBy, direction)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Filtered by age range")
                .data(users)
                .build();
    }

    @PutMapping("/update-location/{id}")
    @Operation(summary = "Update location", description = "Change employee location")
    public ApiResponse<UserResponse> updateLocation(@PathVariable Long id, @RequestParam String location) {
        User user = managerService.updateLocation(id, location);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Location updated")
                .data(employeeService.mapToDto(user))
                .build();
    }

    @GetMapping("/team")
    @Operation(summary = "My team", description = "Show manager team")
    public ApiResponse<java.util.List<UserResponse>> getMyTeam() {
        java.util.List<UserResponse> users = managerService.getMyTeam().stream()
                .map(employeeService::mapToDto)
                .toList();

        return ApiResponse.<java.util.List<UserResponse>>builder()
                .success(true)
                .message("Manager team fetched successfully")
                .data(users)
                .build();
    }
}

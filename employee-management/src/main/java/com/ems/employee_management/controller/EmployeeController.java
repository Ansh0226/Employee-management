package com.ems.employee_management.controller;

import com.ems.employee_management.dto.ApiResponse;
import com.ems.employee_management.dto.ChangePasswordRequest;
import com.ems.employee_management.dto.UpdateUserRequest;
import com.ems.employee_management.dto.UserResponse;
import com.ems.employee_management.entity.User;
import com.ems.employee_management.service.EmployeeService;
import com.ems.employee_management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserService userService;

    // ✅ Get all (pagination + sorting)
    @GetMapping
    public ApiResponse<Page<UserResponse>> getAll(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<UserResponse> users = employeeService
                .getAllEmployees(page, size, sortBy, direction)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Users fetched successfully")
                .data(users)
                .build();
    }

    // ✅ Search (multi-field)
    @GetMapping("/search")
    public ApiResponse<Page<UserResponse>> search(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int size) {

        Page<UserResponse> users = employeeService
                .search(keyword, page, size)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Users searched successfully")
                .data(users)
                .build();
    }

    // ✅ Filter by location
    @GetMapping("/filter/location")
    public ApiResponse<Page<UserResponse>> filterByLocation(
            @RequestParam String location,
            @RequestParam int page,
            @RequestParam int size) {

        Page<UserResponse> users = employeeService
                .filterByLocation(location.trim(), page, size)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Users filtered by location successfully")
                .data(users)
                .build();
    }

    // ✅ Filter by age range (DOB based)
    @GetMapping("/filter/age-range")
    public ApiResponse<Page<UserResponse>> filterByAgeRange(
            @RequestParam int minAge,
            @RequestParam int maxAge,
            @RequestParam int page,
            @RequestParam int size) {

        Page<UserResponse> users = employeeService
                .filterByAgeRange(minAge, maxAge, page, size)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Users filtered by age range successfully")
                .data(users)
                .build();
    }

    // ✅ Get by ID
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {

        UserResponse user = employeeService.mapToDto(
                employeeService.getById(id));

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User fetched successfully")
                .data(user)
                .build();
    }

    // ✅ Update (role-based logic inside service)
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        UserResponse updatedUser = employeeService.mapToDto(
                employeeService.update(id, request));

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User updated successfully")
                .data(updatedUser)
                .build();
    }

    @GetMapping("/profile")
    public ApiResponse<UserResponse> profile() {
        User currentUser = userService.getCurrentUser();

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Profile fetched successfully")
                .data(employeeService.mapToDto(currentUser))
                .build();
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Password updated successfully")
                .data("Password updated")
                .build();
    }

    // ✅ Delete (only ADMIN allowed via SecurityConfig)
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {

        employeeService.delete(id);

        return ApiResponse.<String>builder()
                .success(true)
                .message("User deleted successfully")
                .data("Deleted successfully")
                .build();
    }
}

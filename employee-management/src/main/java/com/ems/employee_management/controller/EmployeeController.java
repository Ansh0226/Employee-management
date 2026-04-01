package com.ems.employee_management.controller;

import com.ems.employee_management.dto.ApiResponse;
import com.ems.employee_management.dto.ChangePasswordRequest;
import com.ems.employee_management.dto.UpdateProfileContactRequest;
import com.ems.employee_management.dto.UpdateProfileImageRequest;
import com.ems.employee_management.dto.UserResponse;
import com.ems.employee_management.entity.User;
import com.ems.employee_management.entity.enums.Role;
import com.ems.employee_management.service.EmployeeService;
import com.ems.employee_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Tag(name = "Employee", description = "Employee directory and profile")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Directory", description = "List approved users")
    public ApiResponse<Page<UserResponse>> getAll(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<UserResponse> users = employeeService
                .getAllEmployees(page, size, role, sortBy, direction)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Users fetched successfully")
                .data(users)
                .build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search directory users")
    public ApiResponse<Page<UserResponse>> search(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Role role) {

        Page<UserResponse> users = employeeService
                .search(keyword, page, size, role)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Users searched successfully")
                .data(users)
                .build();
    }

    @GetMapping("/filter/location")
    @Operation(summary = "Filter location", description = "Filter by location")
    public ApiResponse<Page<UserResponse>> filterByLocation(
            @RequestParam String location,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Role role) {

        Page<UserResponse> users = employeeService
                .filterByLocation(location.trim(), page, size, role)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Users filtered by location successfully")
                .data(users)
                .build();
    }

    @GetMapping("/filter/age-range")
    @Operation(summary = "Filter age", description = "Filter by age range")
    public ApiResponse<Page<UserResponse>> filterByAgeRange(
            @RequestParam int minAge,
            @RequestParam int maxAge,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Role role) {

        Page<UserResponse> users = employeeService
                .filterByAgeRange(minAge, maxAge, page, size, role)
                .map(employeeService::mapToDto);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Users filtered by age range successfully")
                .data(users)
                .build();
    }

    @GetMapping("/profile")
    @Operation(summary = "My profile", description = "Show current profile")
    public ApiResponse<UserResponse> profile() {
        User currentUser = userService.getCurrentUser();

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Profile fetched successfully")
                .data(employeeService.mapToDto(currentUser))
                .build();
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Update own password")
    public ApiResponse<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Password updated successfully")
                .data("Password updated")
                .build();
    }

    @PutMapping("/profile-image")
    @Operation(summary = "Update image", description = "Update profile image")
    public ApiResponse<UserResponse> updateProfileImage(@RequestBody UpdateProfileImageRequest request) {
        User updatedUser = userService.updateProfileImage(request);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Profile image updated successfully")
                .data(employeeService.mapToDto(updatedUser))
                .build();
    }

    @PutMapping("/profile-contact")
    @Operation(summary = "Update contact", description = "Update contact number")
    public ApiResponse<UserResponse> updateProfileContact(@Valid @RequestBody UpdateProfileContactRequest request) {
        User updatedUser = userService.updateProfileContact(request);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Contact number updated successfully")
                .data(employeeService.mapToDto(updatedUser))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete employee or manager")
    public ApiResponse<String> delete(@PathVariable Long id) {
        employeeService.delete(id);

        return ApiResponse.<String>builder()
                .success(true)
                .message("User deleted successfully")
                .data("Deleted successfully")
                .build();
    }
}

package com.ems.employee_management.service;

import com.ems.employee_management.dto.UpdateUserRequest;
import com.ems.employee_management.dto.UserResponse;
import com.ems.employee_management.entity.User;
import com.ems.employee_management.entity.enums.Role;
import com.ems.employee_management.entity.enums.Status;
import com.ems.employee_management.exception.BadRequestException;
import com.ems.employee_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final UserRepository userRepository;

    public Page<User> getAllEmployees(int page, int size, Role roleFilter, String sortBy, String direction) {
        List<User> users = getApprovedDirectoryUsers();
        users = applyRoleFilter(users, roleFilter);
        users = sortUsers(users, sortBy, direction);
        return toPage(users, page, size);
    }

    public Page<User> search(String keyword, int page, int size, Role roleFilter) {
        String searchText = keyword.trim().toLowerCase();

        List<User> users = getApprovedDirectoryUsers().stream()
                .filter(user -> matchesKeyword(user, searchText))
                .toList();

        users = applyRoleFilter(users, roleFilter);
        return toPage(users, page, size);
    }

    public Page<User> filterByLocation(String location, int page, int size, Role roleFilter) {
        String cleanLocation = location.trim();

        List<User> users = getApprovedDirectoryUsers().stream()
                .filter(user -> cleanLocation.equalsIgnoreCase(user.getLocation()))
                .toList();

        users = applyRoleFilter(users, roleFilter);
        return toPage(users, page, size);
    }

    public Page<User> filterByAgeRange(int minAge, int maxAge, int page, int size, Role roleFilter) {
        List<User> users = getApprovedDirectoryUsers().stream()
                .filter(user -> user.getDob() != null)
                .filter(user -> {
                    int age = Period.between(user.getDob(), LocalDate.now()).getYears();
                    return age >= minAge && age <= maxAge;
                })
                .toList();

        users = applyRoleFilter(users, roleFilter);
        return toPage(users, page, size);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    public User update(Long id, UpdateUserRequest request) {
        User user = getById(id);

        String role = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .toString();

        if (role.contains("ROLE_EMPLOYEE")) {
            user.setEmail(request.getEmail());
            user.setContactNumber(request.getContactNumber());
            user.setDob(request.getDob());
        } else if (role.contains("ROLE_MANAGER")) {
            user.setLocation(request.getLocation());
        } else if (role.contains("ROLE_ADMIN")) {
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setContactNumber(request.getContactNumber());
            user.setLocation(request.getLocation());
            user.setDob(request.getDob());
        }

        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponse mapToDto(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .employeeId(user.getEmployeeId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .contactNumber(user.getContactNumber())
                .location(user.getLocation())
                .profileImage(user.getProfileImage())
                .dob(user.getDob())
                .age(user.getAge())
                .managerId(user.getManager() != null ? user.getManager().getId() : null)
                .managerName(user.getManager() != null
                        ? user.getManager().getFirstName() + " " + user.getManager().getLastName()
                        : (user.getRole() == Role.MANAGER ? "Admin" : null))
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
    }

    private List<User> getApprovedDirectoryUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getStatus() == Status.APPROVED)
                .filter(user -> user.getRole() != Role.ADMIN)
                .toList();
    }

    private List<User> applyRoleFilter(List<User> users, Role roleFilter) {
        if (roleFilter == null) {
            return users;
        }

        return users.stream()
                .filter(user -> user.getRole() == roleFilter)
                .toList();
    }

    private List<User> sortUsers(List<User> users, String sortBy, String direction) {
        Comparator<User> comparator = Comparator.comparing(
                user -> getSortableValue(user, sortBy),
                String.CASE_INSENSITIVE_ORDER
        );

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return users.stream()
                .sorted(comparator)
                .toList();
    }

    private String getSortableValue(User user, String sortBy) {
        if ("employeeId".equalsIgnoreCase(sortBy)) {
            return defaultValue(user.getEmployeeId());
        }

        return defaultValue(user.getFirstName());
    }

    private String defaultValue(String value) {
        return value == null ? "" : value;
    }

    private boolean matchesKeyword(User user, String keyword) {
        return contains(user.getUsername(), keyword)
                || contains(user.getEmail(), keyword)
                || contains(user.getEmployeeId(), keyword)
                || contains(user.getFirstName(), keyword)
                || contains(user.getLastName(), keyword)
                || contains(user.getContactNumber(), keyword)
                || contains(user.getLocation(), keyword);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private Page<User> toPage(List<User> users, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        int start = Math.min((int) pageable.getOffset(), users.size());
        int end = Math.min(start + pageable.getPageSize(), users.size());

        return new PageImpl<>(users.subList(start, end), pageable, users.size());
    }
}

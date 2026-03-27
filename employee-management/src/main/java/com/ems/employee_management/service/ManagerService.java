package com.ems.employee_management.service;

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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final UserRepository userRepository;
    private final UserService userService;

    public Page<User> getApprovedEmployees(int page, int size, String sortBy, String direction) {
        List<User> users = getApprovedEmployeesList();
        users = sortUsers(users, sortBy, direction);

        return toPage(users, page, size);
    }

    public Page<User> searchApproved(String keyword, int page, int size, String sortBy, String direction) {
        String searchText = keyword.trim().toLowerCase();

        List<User> users = getApprovedEmployeesList().stream()
                .filter(user -> matchesKeyword(user, searchText))
                .toList();

        users = sortUsers(users, sortBy, direction);
        return toPage(users, page, size);
    }

    public Page<User> filterByLocation(String location, int page, int size, String sortBy, String direction) {
        String cleanLocation = location.trim();

        List<User> users = getApprovedEmployeesList().stream()
                .filter(user -> cleanLocation.equalsIgnoreCase(user.getLocation()))
                .toList();

        users = sortUsers(users, sortBy, direction);
        return toPage(users, page, size);
    }

    public Page<User> filterByAgeRange(int minAge, int maxAge, int page, int size, String sortBy, String direction) {
        List<User> users = getApprovedEmployeesList().stream()
                .filter(user -> user.getDob() != null)
                .filter(user -> {
                    int age = Period.between(user.getDob(), LocalDate.now()).getYears();
                    return age >= minAge && age <= maxAge;
                })
                .toList();

        users = sortUsers(users, sortBy, direction);
        return toPage(users, page, size);
    }

    public User updateLocation(Long id, String location) {
        User manager = userService.getCurrentUser();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getRole() != Role.EMPLOYEE) {
            throw new BadRequestException("Managers can update location for employees only");
        }

        if (user.getManager() == null || !user.getManager().getId().equals(manager.getId())) {
            throw new BadRequestException("Managers can update their own team only");
        }

        user.setLocation(location);
        return userRepository.save(user);
    }

    public List<User> getMyTeam() {
        User manager = userService.getCurrentUser();
        return userRepository.findByManagerIdAndRole(manager.getId(), Role.EMPLOYEE);
    }

    private List<User> getApprovedEmployeesList() {
        return userRepository.findAll().stream()
                .filter(user -> user.getStatus() == Status.APPROVED)
                .filter(user -> user.getRole() == Role.EMPLOYEE)
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

        if ("email".equalsIgnoreCase(sortBy)) {
            return defaultValue(user.getEmail());
        }

        if ("location".equalsIgnoreCase(sortBy)) {
            return defaultValue(user.getLocation());
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

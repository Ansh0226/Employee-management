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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final UserRepository userRepository;

    public Page<User> getApprovedEmployees(int page, int size) {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getStatus() == Status.APPROVED)
                .filter(user -> user.getRole() == Role.EMPLOYEE)
                .toList();

        return toPage(users, page, size);
    }

    public Page<User> searchApproved(String keyword, int page, int size) {
        String searchText = keyword.trim().toLowerCase();

        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getStatus() == Status.APPROVED)
                .filter(user -> user.getRole() == Role.EMPLOYEE)
                .filter(user -> matchesKeyword(user, searchText))
                .toList();

        return toPage(users, page, size);
    }

    public Page<User> filterByLocation(String location, int page, int size) {
        String cleanLocation = location.trim();

        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getStatus() == Status.APPROVED)
                .filter(user -> user.getRole() == Role.EMPLOYEE)
                .filter(user -> cleanLocation.equalsIgnoreCase(user.getLocation()))
                .toList();

        return toPage(users, page, size);
    }

    public User updateLocation(Long id, String location) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getRole() != Role.EMPLOYEE) {
            throw new BadRequestException("Managers can update location for employees only");
        }

        user.setLocation(location);
        return userRepository.save(user);
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

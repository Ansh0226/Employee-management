package com.ems.employee_management.service;

import com.ems.employee_management.entity.User;
import com.ems.employee_management.entity.enums.Role;
import com.ems.employee_management.entity.enums.Status;
import com.ems.employee_management.exception.BadRequestException;
import com.ems.employee_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    // ✅ Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Approve user
    public User approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getStatus() == Status.APPROVED) {
            throw new BadRequestException("User already approved");
        }

        user.setStatus(Status.APPROVED);
        return userRepository.save(user);
    }

    // ✅ Change role
    public User changeRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        user.setRole(newRole);

        if (newRole != Role.EMPLOYEE) {
            user.setManager(null);
        }

        return userRepository.save(user);
    }

    public User assignEmployeeToManager(Long employeeId, Long managerId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new BadRequestException("Employee not found"));

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new BadRequestException("Manager not found"));

        if (employee.getRole() != Role.EMPLOYEE) {
            throw new BadRequestException("Selected user is not an employee");
        }

        if (employee.getStatus() != Status.APPROVED) {
            throw new BadRequestException("Only approved employees can be assigned");
        }

        if (manager.getRole() != Role.MANAGER) {
            throw new BadRequestException("Selected user is not a manager");
        }

        if (manager.getStatus() != Status.APPROVED) {
            throw new BadRequestException("Only approved managers can receive team assignments");
        }

        employee.setManager(manager);
        return userRepository.save(employee);
    }

    public List<User> getManagers() {
        return userRepository.findByRole(Role.MANAGER).stream()
                .filter(user -> user.getStatus() == Status.APPROVED)
                .toList();
    }

    public List<User> getManagerTeam(Long managerId) {
        return userRepository.findByManagerIdAndRole(managerId, Role.EMPLOYEE).stream()
                .filter(user -> user.getStatus() == Status.APPROVED)
                .toList();
    }
}

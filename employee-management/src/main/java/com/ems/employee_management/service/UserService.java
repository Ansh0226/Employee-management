package com.ems.employee_management.service;

import com.ems.employee_management.dto.SignupRequest;
import com.ems.employee_management.dto.ChangePasswordRequest;
import com.ems.employee_management.dto.UpdateProfileImageRequest;
import com.ems.employee_management.entity.User;
import com.ems.employee_management.entity.enums.Role;
import com.ems.employee_management.entity.enums.Status;
import com.ems.employee_management.exception.BadRequestException;
import com.ems.employee_management.exception.ResourceAlreadyExistsException;
import com.ems.employee_management.repository.UserRepository;
import com.ems.employee_management.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import com.ems.employee_management.dto.LoginRequest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;
    // 🔥 Signup Logic
    public User signup(SignupRequest request) {

        // ✅ Convert username to lowercase
        String username = request.getUsername().toLowerCase();

        // ✅ Check duplicates
        if (userRepository.existsByUsername(username)) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        if (userRepository.existsByContactNumber(request.getContactNumber())) {
            throw new ResourceAlreadyExistsException("Contact number already exists");
        }

        // ✅ Default last name
        String lastName = request.getLastName();
        if (lastName == null || lastName.isBlank()) {
            lastName = "X";
        }

        // ✅ Generate Employee ID
        String employeeId = generateEmployeeId();

        // ✅ Build user
        User user = User.builder()
                .employeeId(employeeId)
                .username(username)
                .firstName(request.getFirstName())
                .lastName(lastName)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .dob(request.getDob())
                .contactNumber(request.getContactNumber())
                .location(request.getLocation())
                .profileImage(request.getProfileImage())
                .role(Role.EMPLOYEE)
                .status(Status.PENDING)
                .build();

        return userRepository.save(user);
    }

    // 🔢 Employee ID Generator
    private String generateEmployeeId() {
        long count = userRepository.count() + 1;
        return String.format("EMP%03d", count);
    }

    // login logic
    public String login(LoginRequest request) {

        String identifier = request.getIdentifier();

        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .or(() -> userRepository.findByEmployeeId(identifier))
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        if (user.getStatus() != Status.APPROVED) {
            throw new BadRequestException("User not approved");
        }

        if (request.getRole() != null && user.getRole() != request.getRole()) {
            throw new BadRequestException("Your role is wrong");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRole().name());
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is wrong");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public User updateProfileImage(UpdateProfileImageRequest request) {
        User user = getCurrentUser();
        user.setProfileImage(request.getProfileImage());
        return userRepository.save(user);
    }
}

package com.ems.employee_management.config;

import com.ems.employee_management.entity.User;
import com.ems.employee_management.entity.enums.Role;
import com.ems.employee_management.entity.enums.Status;
import com.ems.employee_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {

        if (!userRepository.existsByUsername("admin")) {

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            User admin = User.builder()
                    .employeeId("EMP000")
                    .username("admin")
                    .firstName("Admin")
                    .lastName("X")
                    .email("admin@gmail.com")
                    .password(encoder.encode("Admin@123"))
                    .dob(java.time.LocalDate.of(2004, 2, 26))
                    .contactNumber("9999999999")
                    .location("India")
                    .role(Role.ADMIN)
                    .status(Status.APPROVED)
                    .build();

            userRepository.save(admin);
        }
    }
}
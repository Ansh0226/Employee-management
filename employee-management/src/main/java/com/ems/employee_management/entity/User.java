package com.ems.employee_management.entity;

import java.time.LocalDate;
import java.time.Period;

import com.ems.employee_management.entity.enums.Role;
import com.ems.employee_management.entity.enums.Status;
import com.ems.employee_management.validation.Adult;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Employee ID (AUTO GENERATED)
    @Column(unique = true, nullable = false)
    private String employeeId;

    // ✅ Username
    @NotBlank
    @Pattern(regexp = "^(?=.{3,20}$)(?![._])(?!.*[._]{2})[a-z0-9._]+(?<![._])$", message = "Invalid username format")
    @Column(unique = true, nullable = false)
    private String username;

    // ✅ First Name
    @NotBlank(message = "First name is required")
    private String firstName;

    // ❗ Optional Last Name
    private String lastName;

    // ✅ Email
    @NotBlank
    @Email(message = "Invalid email")
    @Column(unique = true, nullable = false)
    private String email;

    // ✅ Password
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Password must have 8 chars, 1 uppercase, 1 lowercase, 1 number")
    private String password;

    // ✅ Age
    @NotNull
    @Past(message = "DOB must be in the past")
    @Adult
    private LocalDate dob;

    // ✅ Contact Number (INDIA friendly validation)
    @NotBlank
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Contact number must be 10 digits and valid")
    @Column(unique = true, nullable = false)
    private String contactNumber;

    private String location;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    public int getAge() {
    return Period.between(this.dob, LocalDate.now()).getYears();
}
}

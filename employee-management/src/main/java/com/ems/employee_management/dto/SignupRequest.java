package com.ems.employee_management.dto;

import java.time.LocalDate;

import com.ems.employee_management.validation.Adult;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank
    @Pattern(regexp = "^(?=.{3,20}$)(?![._])(?!.*[._]{2})[a-z0-9._]+(?<![._])$", message = "Invalid username format")
    private String username;

    @NotBlank
    private String firstName;

    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Weak password")
    private String password;

    @NotNull
    @Past(message = "DOB must be in the past")
    @Adult
    private LocalDate dob;

    @NotBlank
    @Pattern(regexp = "^[6-9]\\d{9}$")
    private String contactNumber;

    private String location;

    private String profileImage;
}

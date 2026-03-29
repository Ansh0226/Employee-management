package com.ems.employee_management.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String employeeId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String location;
    private String profileImage;
    private LocalDate dob;
    private Integer age;
    private Long managerId;
    private String managerName;
    private String role;
    private String status;
}

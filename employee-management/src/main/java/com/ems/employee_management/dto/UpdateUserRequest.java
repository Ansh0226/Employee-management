package com.ems.employee_management.dto;

import com.ems.employee_management.validation.Adult;
import jakarta.validation.constraints.Past;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String location;
    @Past(message = "DOB must be in the past")
    @Adult
    private LocalDate dob;
}

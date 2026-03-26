package com.ems.employee_management.service;

import com.ems.employee_management.dto.UpdateUserRequest;
import com.ems.employee_management.dto.UserResponse;
import com.ems.employee_management.entity.User;
import com.ems.employee_management.entity.enums.Status;
import com.ems.employee_management.exception.BadRequestException;
import com.ems.employee_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final UserRepository userRepository;

    // ✅ Get All with Pagination + Sorting
    public Page<User> getAllEmployees(int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        String role = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .toString();

        // 👨‍💼 MANAGER → only APPROVED users
        if (role.contains("ROLE_MANAGER")) {
            return userRepository.findByStatus(Status.APPROVED, pageable);
        }

        // 👑 ADMIN → all users
        return userRepository.findAll(pageable);
    }

    // ✅ Search by multiple fields
    public Page<User> search(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return userRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrEmployeeIdContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrContactNumberContainingIgnoreCaseOrLocationContainingIgnoreCase(
                        keyword, keyword, keyword, keyword, keyword, keyword, keyword, pageable);
    }

    

    // ✅ Filter by location
    public Page<User> filterByLocation(String location, int page, int size) {
        location = location.trim();
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findByLocationIgnoreCase(location, pageable);
    }

    // ✅ Filter by age
    public Page<User> filterByAgeRange(int minAge, int maxAge, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        List<User> users = userRepository.findAll();

        List<User> filtered = users.stream()
                .filter(user -> {
                    if (user.getDob() == null)
                        return false;

                    int age = Period.between(user.getDob(), LocalDate.now()).getYears();

                    return age >= minAge && age <= maxAge;
                })
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());

        List<User> pagedList = filtered.subList(start, end);

        return new PageImpl<>(pagedList, pageable, filtered.size());
    }

  

    // ✅ Get by ID
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    // ✅ Update employee
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
    }

    else if (role.contains("ROLE_MANAGER")) {
        user.setLocation(request.getLocation());
    }

    else if (role.contains("ROLE_ADMIN")) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setContactNumber(request.getContactNumber());
        user.setLocation(request.getLocation());
        user.setDob(request.getDob());
    }

    return userRepository.save(user);
}
    // ✅ Delete employee
    public void delete(Long id) {
        userRepository.deleteById(id);
    }


    //map to dta transfer object
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
                .age(user.getAge()) // calculated from DOB
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
    }
}

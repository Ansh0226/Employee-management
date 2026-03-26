package com.ems.employee_management.repository;

import com.ems.employee_management.entity.User;
import com.ems.employee_management.entity.enums.Status;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmployeeId(String employeeId);

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Page<User> findByLocationIgnoreCase(String location, Pageable pageable);

    Page<User> findByAgeBetween(int minAge, int maxAge, Pageable pageable);

    Page<User> findByStatus(Status status, Pageable pageable);
    // smart search
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrEmployeeIdContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrContactNumberContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String username,
            String email,
            String employeeId,
            String firstName,
            String lastName,
            String contactNumber,
            String location,
            Pageable pageable);
            
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByContactNumber(String contactNumber);
}

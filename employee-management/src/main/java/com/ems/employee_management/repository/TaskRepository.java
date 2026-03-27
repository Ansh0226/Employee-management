package com.ems.employee_management.repository;

import com.ems.employee_management.entity.Task;
import com.ems.employee_management.entity.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByManagerId(Long managerId);
    List<Task> findByEmployeeId(Long employeeId);
    List<Task> findByManagerIdAndStatus(Long managerId, TaskStatus status);
    List<Task> findByProjectId(Long projectId);
}

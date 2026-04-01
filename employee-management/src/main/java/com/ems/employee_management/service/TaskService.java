package com.ems.employee_management.service;

import com.ems.employee_management.dto.CreateTaskRequest;
import com.ems.employee_management.dto.TaskResponse;
import com.ems.employee_management.dto.UpdateTaskStatusRequest;
import com.ems.employee_management.entity.Project;
import com.ems.employee_management.entity.Task;
import com.ems.employee_management.entity.User;
import com.ems.employee_management.entity.enums.Role;
import com.ems.employee_management.entity.enums.TaskStatus;
import com.ems.employee_management.exception.BadRequestException;
import com.ems.employee_management.repository.TaskRepository;
import com.ems.employee_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final UserService userService;

    public Task createTask(CreateTaskRequest request) {
        User manager = userService.getCurrentUser();

        if (manager.getRole() != Role.MANAGER) {
            throw new BadRequestException("Only managers can create tasks");
        }

        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new BadRequestException("Employee not found"));

        if (employee.getRole() != Role.EMPLOYEE) {
            throw new BadRequestException("Tasks can be assigned to employees only");
        }

        if (employee.getManager() == null || !employee.getManager().getId().equals(manager.getId())) {
            throw new BadRequestException("You can assign tasks to your own team only");
        }

        Project project = projectService.getProject(request.getProjectId());
        if (project.getManager() == null || !project.getManager().getId().equals(manager.getId())) {
            throw new BadRequestException("You can create tasks for your own projects only");
        }

        Task task = Task.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .status(TaskStatus.ASSIGNED)
                .project(project)
                .manager(manager)
                .employee(employee)
                .build();

        return taskRepository.save(task);
    }

    public List<Task> getManagerTasks() {
        User manager = userService.getCurrentUser();
        return taskRepository.findByManagerId(manager.getId());
    }

    public List<Task> getEmployeeTasks() {
        User employee = userService.getCurrentUser();
        return taskRepository.findByEmployeeId(employee.getId());
    }

    public Task updateEmployeeTaskStatus(Long taskId, UpdateTaskStatusRequest request) {
        User employee = userService.getCurrentUser();
        Task task = getTask(taskId);

        if (!task.getEmployee().getId().equals(employee.getId())) {
            throw new BadRequestException("You can update your own tasks only");
        }

        if (request.getStatus() != TaskStatus.COMPLETED) {
            throw new BadRequestException("Employees can mark tasks as completed only");
        }

        task.setStatus(TaskStatus.COMPLETED);
        return taskRepository.save(task);
    }

    public Task approveTask(Long taskId) {
        User manager = userService.getCurrentUser();
        Task task = getTask(taskId);

        if (!task.getManager().getId().equals(manager.getId())) {
            throw new BadRequestException("You can approve tasks for your own team only");
        }

        if (task.getStatus() != TaskStatus.COMPLETED) {
            throw new BadRequestException("Only completed tasks can be approved");
        }

        task.setStatus(TaskStatus.APPROVED);
        return taskRepository.save(task);
    }

    public void deleteManagerTask(Long taskId) {
        User manager = userService.getCurrentUser();
        Task task = getTask(taskId);

        if (!task.getManager().getId().equals(manager.getId())) {
            throw new BadRequestException("You can delete your own tasks only");
        }

        if (task.getStatus() == TaskStatus.APPROVED) {
            throw new BadRequestException("Approved tasks cannot be deleted");
        }

        taskRepository.delete(task);
    }

    public Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new BadRequestException("Task not found"));
    }

    public TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
                .managerId(task.getManager().getId())
                .managerName(task.getManager().getFirstName() + " " + task.getManager().getLastName())
                .employeeId(task.getEmployee().getId())
                .employeeName(task.getEmployee().getFirstName() + " " + task.getEmployee().getLastName())
                .build();
    }
}

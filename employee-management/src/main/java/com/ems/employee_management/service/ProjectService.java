package com.ems.employee_management.service;

import com.ems.employee_management.dto.AssignProjectManagerRequest;
import com.ems.employee_management.dto.CreateProjectRequest;
import com.ems.employee_management.dto.ProjectDetailResponse;
import com.ems.employee_management.dto.ProjectResponse;
import com.ems.employee_management.dto.UserResponse;
import com.ems.employee_management.entity.Project;
import com.ems.employee_management.entity.Task;
import com.ems.employee_management.entity.User;
import com.ems.employee_management.entity.enums.ProjectStatus;
import com.ems.employee_management.entity.enums.Role;
import com.ems.employee_management.exception.BadRequestException;
import com.ems.employee_management.repository.ProjectRepository;
import com.ems.employee_management.repository.TaskRepository;
import com.ems.employee_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final EmployeeService employeeService;

    public Project createProject(CreateProjectRequest request) {
        Project project = Project.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .status(ProjectStatus.ACTIVE)
                .build();

        return projectRepository.save(project);
    }

    public Project assignManager(AssignProjectManagerRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new BadRequestException("Project not found"));

        User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new BadRequestException("Manager not found"));

        if (manager.getRole() != Role.MANAGER) {
            throw new BadRequestException("Selected user is not a manager");
        }

        project.setManager(manager);
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsForManager(Long managerId) {
        return projectRepository.findByManagerId(managerId);
    }

    public List<Project> getMyProjects() {
        User manager = userService.getCurrentUser();
        return projectRepository.findByManagerId(manager.getId());
    }

    public List<Project> getProjectsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            return projectRepository.findAll();
        }

        if (user.getRole() == Role.MANAGER) {
            return projectRepository.findByManagerId(userId);
        }

        if (user.getManager() != null) {
            return projectRepository.findByManagerId(user.getManager().getId());
        }

        Map<Long, Project> projects = new LinkedHashMap<>();
        for (Task task : taskRepository.findByEmployeeId(userId)) {
            projects.put(task.getProject().getId(), task.getProject());
        }
        return projects.values().stream().toList();
    }

    public List<Project> getProfileProjects() {
        User currentUser = userService.getCurrentUser();
        return getProjectsForUser(currentUser.getId());
    }

    public Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Project not found"));
    }

    public ProjectResponse toResponse(Project project) {
        User manager = project.getManager();
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus().name())
                .managerId(manager != null ? manager.getId() : null)
                .managerName(manager != null ? manager.getFirstName() + " " + manager.getLastName() : null)
                .build();
    }

    public ProjectDetailResponse toDetailResponse(Project project) {
        Map<Long, UserResponse> team = new LinkedHashMap<>();
        User manager = project.getManager();
        if (manager != null) {
            for (User employee : userRepository.findByManagerIdAndRole(manager.getId(), Role.EMPLOYEE)) {
                team.put(employee.getId(), employeeService.mapToDto(employee));
            }
        } else {
            for (Task task : taskRepository.findByProjectId(project.getId())) {
                User employee = task.getEmployee();
                team.put(employee.getId(), employeeService.mapToDto(employee));
            }
        }

        return ProjectDetailResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus().name())
                .managerId(manager != null ? manager.getId() : null)
                .managerName(manager != null ? manager.getFirstName() + " " + manager.getLastName() : "Admin")
                .teamMembers(team.values().stream().toList())
                .build();
    }
}

import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import {
  ApiResponse,
  AssignEmployeeManagerRequest,
  AssignProjectManagerRequest,
  CreateProjectRequest,
  ProjectDetailRecord,
  CreateTaskRequest,
  ProjectRecord,
  TaskRecord,
  UpdateTaskStatusRequest,
  UserRecord
} from './models';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class WorkflowService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);
  private readonly apiBase = 'http://localhost:8080';

  getManagers() {
    return this.http.get<ApiResponse<UserRecord[]>>(`${this.apiBase}/admin/managers`, { headers: this.authHeaders() });
  }

  assignEmployeeToManager(payload: AssignEmployeeManagerRequest) {
    return this.http.put<ApiResponse<UserRecord>>(`${this.apiBase}/admin/assign-manager`, payload, { headers: this.authHeaders() });
  }

  getManagerTeam(managerId: number) {
    return this.http.get<ApiResponse<UserRecord[]>>(`${this.apiBase}/admin/manager-team/${managerId}`, { headers: this.authHeaders() });
  }

  getMyTeam() {
    return this.http.get<ApiResponse<UserRecord[]>>(`${this.apiBase}/manager/team`, { headers: this.authHeaders() });
  }

  getProjects() {
    return this.http.get<ApiResponse<ProjectRecord[]>>(`${this.apiBase}/projects`, { headers: this.authHeaders() });
  }

  createProject(payload: CreateProjectRequest) {
    return this.http.post<ApiResponse<ProjectRecord>>(`${this.apiBase}/projects`, payload, { headers: this.authHeaders() });
  }

  assignProjectManager(payload: AssignProjectManagerRequest) {
    return this.http.put<ApiResponse<ProjectRecord>>(`${this.apiBase}/projects/assign-manager`, payload, { headers: this.authHeaders() });
  }

  getProjectsForManager(managerId: number) {
    return this.http.get<ApiResponse<ProjectRecord[]>>(`${this.apiBase}/projects/manager/${managerId}`, { headers: this.authHeaders() });
  }

  getMyProjects() {
    return this.http.get<ApiResponse<ProjectRecord[]>>(`${this.apiBase}/projects/my`, { headers: this.authHeaders() });
  }

  getProjectsForUser(userId: number) {
    return this.http.get<ApiResponse<ProjectRecord[]>>(`${this.apiBase}/projects/user/${userId}`, { headers: this.authHeaders() });
  }

  getProfileProjects() {
    return this.http.get<ApiResponse<ProjectRecord[]>>(`${this.apiBase}/projects/profile`, { headers: this.authHeaders() });
  }

  getProjectDetail(projectId: number) {
    return this.http.get<ApiResponse<ProjectDetailRecord>>(`${this.apiBase}/projects/${projectId}/detail`, { headers: this.authHeaders() });
  }

  createTask(payload: CreateTaskRequest) {
    return this.http.post<ApiResponse<TaskRecord>>(`${this.apiBase}/tasks`, payload, { headers: this.authHeaders() });
  }

  getManagerTasks() {
    return this.http.get<ApiResponse<TaskRecord[]>>(`${this.apiBase}/tasks/manager`, { headers: this.authHeaders() });
  }

  getEmployeeTasks() {
    return this.http.get<ApiResponse<TaskRecord[]>>(`${this.apiBase}/tasks/employee`, { headers: this.authHeaders() });
  }

  getPendingTaskApprovals() {
    return this.http.get<ApiResponse<TaskRecord[]>>(`${this.apiBase}/tasks/pending-approvals`, { headers: this.authHeaders() });
  }

  approveTask(taskId: number) {
    return this.http.put<ApiResponse<TaskRecord>>(`${this.apiBase}/tasks/manager/${taskId}/approve`, {}, { headers: this.authHeaders() });
  }

  updateEmployeeLocation(userId: number, location: string) {
    return this.http.put<ApiResponse<UserRecord>>(
      `${this.apiBase}/manager/update-location/${userId}?location=${encodeURIComponent(location)}`,
      {},
      { headers: this.authHeaders() }
    );
  }

  markTaskCompleted(taskId: number) {
    const payload: UpdateTaskStatusRequest = { status: 'COMPLETED' };
    return this.http.put<ApiResponse<TaskRecord>>(`${this.apiBase}/tasks/employee/${taskId}/status`, payload, { headers: this.authHeaders() });
  }

  private authHeaders() {
    return {
      Authorization: `Bearer ${this.auth.token()}`
    };
  }
}

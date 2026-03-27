import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { CreateTaskRequest, ProjectRecord, TaskRecord, UserRecord } from '../../core/models';
import { WorkflowService } from '../../core/workflow.service';
import { DashboardLayoutComponent } from '../../features/dashboard/components/dashboard-layout/dashboard-layout.component';

@Component({
  selector: 'app-manager-tasks-page',
  imports: [CommonModule, FormsModule, DashboardLayoutComponent],
  templateUrl: './manager-tasks-page.component.html',
  styleUrl: './manager-tasks-page.component.scss'
})
export class ManagerTasksPageComponent {
  private readonly workflow = inject(WorkflowService);
  protected readonly team = signal<UserRecord[]>([]);
  protected readonly projects = signal<ProjectRecord[]>([]);
  protected readonly tasks = signal<TaskRecord[]>([]);
  protected readonly approvals = signal<TaskRecord[]>([]);
  protected readonly createModalOpen = signal(false);
  protected readonly selectedTask = signal<TaskRecord | null>(null);
  protected readonly taskFilter = signal<'PENDING' | 'WAITING' | 'COMPLETED'>('PENDING');
  protected readonly notice = signal('');
  protected readonly noticeTone = signal<'success' | 'error'>('success');
  protected readonly taskForm: CreateTaskRequest = { title: '', description: '', projectId: 0, employeeId: 0 };
  protected readonly menuItems = [
    { label: 'Directory', route: '/manager', note: 'See approved employees in your directory.' },
    { label: 'Team', route: '/manager/team', note: 'View your assigned team.' },
    { label: 'Projects', route: '/manager/projects', note: 'See your assigned projects.' },
    { label: 'Tasks', route: '/manager/tasks', note: 'Create tasks and approve completed work.' }
  ];
  protected readonly filteredTasks = computed(() => {
    if (this.taskFilter() === 'WAITING') {
      return this.tasks().filter((task) => task.status === 'COMPLETED');
    }

    if (this.taskFilter() === 'COMPLETED') {
      return this.tasks().filter((task) => task.status === 'APPROVED');
    }

    return this.tasks().filter((task) => task.status === 'ASSIGNED');
  });

  constructor() {
    this.refresh();
  }

  protected refresh(): void {
    this.workflow.getMyTeam().subscribe({ next: (response) => this.team.set(response.data ?? []) });
    this.workflow.getMyProjects().subscribe({ next: (response) => this.projects.set(response.data ?? []) });
    this.workflow.getManagerTasks().subscribe({ next: (response) => this.tasks.set(response.data ?? []) });
    this.workflow.getPendingTaskApprovals().subscribe({ next: (response) => this.approvals.set(response.data ?? []) });
  }

  protected createTask(): void {
    this.workflow.createTask(this.taskForm).subscribe({
      next: (response) => {
        this.notice.set(response.message);
        this.noticeTone.set('success');
        this.taskForm.title = '';
        this.taskForm.description = '';
        this.taskForm.projectId = 0;
        this.taskForm.employeeId = 0;
        this.createModalOpen.set(false);
        this.refresh();
      },
      error: () => {
        this.notice.set('Unable to create task.');
        this.noticeTone.set('error');
      }
    });
  }

  protected openCreateModal(): void {
    this.createModalOpen.set(true);
  }

  protected closeCreateModal(): void {
    this.createModalOpen.set(false);
  }

  protected openTask(task: TaskRecord): void {
    this.selectedTask.set(task);
  }

  protected closeTaskModal(): void {
    this.selectedTask.set(null);
  }

  protected approveTask(taskId: number): void {
    this.workflow.approveTask(taskId).subscribe({
      next: (response) => {
        this.notice.set(response.message);
        this.noticeTone.set('success');
        this.refresh();
      },
      error: () => {
        this.notice.set('Unable to approve task.');
        this.noticeTone.set('error');
      }
    });
  }
}

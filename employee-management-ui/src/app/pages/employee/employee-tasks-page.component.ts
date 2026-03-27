import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';

import { TaskRecord } from '../../core/models';
import { WorkflowService } from '../../core/workflow.service';
import { DashboardLayoutComponent } from '../../features/dashboard/components/dashboard-layout/dashboard-layout.component';

@Component({
  selector: 'app-employee-tasks-page',
  imports: [CommonModule, DashboardLayoutComponent],
  templateUrl: './employee-tasks-page.component.html',
  styleUrl: './employee-tasks-page.component.scss'
})
export class EmployeeTasksPageComponent {
  private readonly workflow = inject(WorkflowService);
  protected readonly tasks = signal<TaskRecord[]>([]);
  protected readonly notice = signal('');
  protected readonly noticeTone = signal<'success' | 'error'>('success');
  protected readonly menuItems = [
    { label: 'Directory', route: '/employee', note: 'Browse the employee directory.' },
    { label: 'Tasks', route: '/employee/tasks', note: 'See and complete your assigned tasks.' },
    { label: 'Exports', route: '/employee/exports', note: 'Download the directory in Excel or PDF.' }
  ];

  constructor() {
    this.refresh();
  }

  protected refresh(): void {
    this.workflow.getEmployeeTasks().subscribe({ next: (response) => this.tasks.set(response.data ?? []) });
  }

  protected completeTask(taskId: number): void {
    this.workflow.markTaskCompleted(taskId).subscribe({
      next: (response) => {
        this.notice.set(response.message);
        this.noticeTone.set('success');
        this.refresh();
      },
      error: () => {
        this.notice.set('Unable to update task.');
        this.noticeTone.set('error');
      }
    });
  }
}

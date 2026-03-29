import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';

import { TaskRecord } from '../../core/models';
import { WorkflowService } from '../../core/workflow.service';
import { EMPLOYEE_MENU_ITEMS } from '../../features/dashboard/dashboard-menu';
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
  protected readonly taskFilter = signal<'ALL' | 'PENDING' | 'WAITING' | 'COMPLETED'>('ALL');
  protected readonly notice = signal('');
  protected readonly noticeTone = signal<'success' | 'error'>('success');
  protected readonly menuItems = EMPLOYEE_MENU_ITEMS;
  protected readonly filteredTasks = computed(() => {
    if (this.taskFilter() === 'ALL') {
      return this.tasks();
    }

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

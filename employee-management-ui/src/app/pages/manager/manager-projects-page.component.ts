import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';

import { ProjectDetailRecord, ProjectRecord } from '../../core/models';
import { WorkflowService } from '../../core/workflow.service';
import { DashboardLayoutComponent } from '../../features/dashboard/components/dashboard-layout/dashboard-layout.component';
import { ProjectDetailModalComponent } from '../../shared/project-detail-modal/project-detail-modal.component';

@Component({
  selector: 'app-manager-projects-page',
  imports: [CommonModule, DashboardLayoutComponent, ProjectDetailModalComponent],
  templateUrl: './manager-projects-page.component.html',
  styleUrl: './manager-projects-page.component.scss'
})
export class ManagerProjectsPageComponent {
  private readonly workflow = inject(WorkflowService);
  protected readonly projects = signal<ProjectRecord[]>([]);
  protected readonly selectedProject = signal<ProjectDetailRecord | null>(null);
  protected readonly menuItems = [
    { label: 'Directory', route: '/manager', note: 'See approved employees in your directory.' },
    { label: 'Team', route: '/manager/team', note: 'View your assigned team.' },
    { label: 'Projects', route: '/manager/projects', note: 'See your assigned projects.' },
    { label: 'Tasks', route: '/manager/tasks', note: 'Create tasks and approve completed work.' },
    { label: 'Location', route: '/manager/location', note: 'Update employee location details.' }
  ];

  constructor() {
    this.refresh();
  }

  protected refresh(): void {
    this.workflow.getMyProjects().subscribe({ next: (response) => this.projects.set(response.data ?? []) });
  }

  protected openProject(projectId: number): void {
    this.workflow.getProjectDetail(projectId).subscribe({
      next: (response) => this.selectedProject.set(response.data)
    });
  }

  protected closeProject(): void {
    this.selectedProject.set(null);
  }
}

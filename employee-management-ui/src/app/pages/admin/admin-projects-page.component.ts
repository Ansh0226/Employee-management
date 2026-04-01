import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { ProjectDetailRecord, ProjectRecord, UserRecord } from '../../core/models';
import { WorkflowService } from '../../core/workflow.service';
import { ADMIN_MENU_ITEMS } from '../../features/dashboard/dashboard-menu';
import { DashboardLayoutComponent } from '../../features/dashboard/components/dashboard-layout/dashboard-layout.component';
import { ProjectDetailModalComponent } from '../../shared/project-detail-modal/project-detail-modal.component';

@Component({
  selector: 'app-admin-projects-page',
  imports: [CommonModule, FormsModule, DashboardLayoutComponent, ProjectDetailModalComponent],
  templateUrl: './admin-projects-page.component.html',
  styleUrl: './admin-projects-page.component.scss'
})
export class AdminProjectsPageComponent {
  private readonly workflow = inject(WorkflowService);
  protected readonly projects = signal<ProjectRecord[]>([]);
  protected readonly managers = signal<UserRecord[]>([]);
  protected readonly createModalOpen = signal(false);
  protected readonly selectedProject = signal<ProjectDetailRecord | null>(null);
  protected readonly deleteErrorMessage = signal('');
  protected readonly pendingDeleteProjectId = signal<number | null>(null);
  protected readonly notice = signal('');
  protected readonly noticeTone = signal<'success' | 'error'>('success');
  protected readonly projectForm = { name: '', description: '' };
  protected readonly assignForm = { projectId: 0, managerId: 0 };
  protected readonly menuItems = ADMIN_MENU_ITEMS;

  constructor() {
    this.refresh();
  }

  protected refresh(): void {
    this.workflow.getProjects().subscribe({ next: (response) => this.projects.set(response.data ?? []) });
    this.workflow.getManagers().subscribe({ next: (response) => this.managers.set(response.data ?? []) });
  }

  protected createProject(): void {
    this.workflow.createProject(this.projectForm).subscribe({
      next: (response) => {
        this.notice.set(response.message);
        this.noticeTone.set('success');
        this.projectForm.name = '';
        this.projectForm.description = '';
        this.createModalOpen.set(false);
        this.refresh();
      },
      error: () => {
        this.notice.set('Unable to create project.');
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

  protected openProject(projectId: number): void {
    this.workflow.getProjectDetail(projectId).subscribe({
      next: (response) => this.selectedProject.set(response.data)
    });
  }

  protected closeProject(): void {
    this.selectedProject.set(null);
  }

  protected deleteProject(projectId: number): void {
    this.pendingDeleteProjectId.set(projectId);
  }

  protected confirmDeleteProject(): void {
    const projectId = this.pendingDeleteProjectId();

    if (!projectId) {
      return;
    }

    this.pendingDeleteProjectId.set(null);

    this.workflow.deleteProject(projectId).subscribe({
      next: (response) => {
        this.notice.set(response.message);
        this.noticeTone.set('success');
        this.selectedProject.set(null);
        this.refresh();
      },
      error: (error) => {
        this.deleteErrorMessage.set(error?.error?.message || 'Unable to delete project.');
      }
    });
  }

  protected assignManager(): void {
    this.workflow.assignProjectManager(this.assignForm).subscribe({
      next: (response) => {
        this.notice.set(response.message);
        this.noticeTone.set('success');
        this.refresh();
      },
      error: () => {
        this.notice.set('Unable to assign project.');
        this.noticeTone.set('error');
      }
    });
  }

  protected closeDeleteErrorModal(): void {
    this.deleteErrorMessage.set('');
  }

  protected closeDeleteConfirmModal(): void {
    this.pendingDeleteProjectId.set(null);
  }
}

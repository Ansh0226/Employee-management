import { CommonModule } from '@angular/common';
import { Component, effect, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { ProjectDetailRecord, ProjectRecord, Role } from '../../../../core/models';
import { WorkflowService } from '../../../../core/workflow.service';
import { ImagePreviewModalComponent } from '../../../../shared/image-preview-modal/image-preview-modal.component';
import { ProjectDetailModalComponent } from '../../../../shared/project-detail-modal/project-detail-modal.component';
import { DashboardStoreService } from '../../services/dashboard-store.service';

@Component({
  selector: 'app-directory-workspace',
  imports: [CommonModule, FormsModule, ImagePreviewModalComponent, ProjectDetailModalComponent],
  templateUrl: './directory-workspace.component.html',
  styleUrl: './directory-workspace.component.scss'
})
export class DirectoryWorkspaceComponent {
  private readonly workflow = inject(WorkflowService);
  readonly store = input.required<DashboardStoreService>();
  readonly title = input.required<string>();
  readonly showRoleEditor = input(false);
  readonly showLocationEditor = input(false);
  readonly showApprovals = input(false);
  readonly showAgeFilters = input(true);
  readonly showSortControls = input(true);
  readonly showExportButtons = input(true);
  readonly showDetailAsModal = input(false);
  protected readonly imagePreviewOpen = signal(false);
  protected readonly selectedProjects = signal<ProjectRecord[]>([]);
  protected readonly selectedProjectDetail = signal<ProjectDetailRecord | null>(null);
  protected readonly deleteErrorMessage = signal('');
  protected readonly pendingDeleteUserId = signal<number | null>(null);
  protected readonly pendingDeleteProjectId = signal<number | null>(null);
  protected readonly roles: Role[] = ['MANAGER', 'EMPLOYEE'];

  constructor() {
    effect(() => {
      const user = this.store().selectedUser();
      if (!user) {
        this.selectedProjects.set([]);
        return;
      }

      this.workflow.getProjectsForUser(user.id).subscribe({
        next: (response) => this.selectedProjects.set(response.data ?? [])
      });
    });
  }

  protected openImagePreview(): void {
    if (this.store().selectedUser()?.profileImage) {
      this.imagePreviewOpen.set(true);
    }
  }

  protected closeImagePreview(): void {
    this.imagePreviewOpen.set(false);
  }

  protected closeDetailModal(): void {
    this.store().selectedUser.set(null);
  }

  protected openProject(projectId: number): void {
    this.workflow.getProjectDetail(projectId).subscribe({
      next: (response) => this.selectedProjectDetail.set(response.data)
    });
  }

  protected closeProjectModal(): void {
    this.selectedProjectDetail.set(null);
  }

  protected deleteSelectedUser(): void {
    const user = this.store().selectedUser();

    if (!user) {
      return;
    }

    this.pendingDeleteUserId.set(user.id);
  }

  protected confirmDeleteSelectedUser(): void {
    const user = this.store().selectedUser();

    if (!user) {
      this.pendingDeleteUserId.set(null);
      return;
    }

    this.pendingDeleteUserId.set(null);

    this.workflow.deleteUser(user.id).subscribe({
      next: (response) => {
        this.store().notice.set(response.message);
        this.store().noticeTone.set('success');
        this.store().selectedUser.set(null);
        this.store().refresh();
      },
      error: (error) => {
        this.deleteErrorMessage.set(error?.error?.message || 'Unable to delete user.');
      }
    });
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
        this.store().notice.set(response.message);
        this.store().noticeTone.set('success');
        this.selectedProjectDetail.set(null);
        this.selectedProjects.update((projects) => projects.filter((project) => project.id !== projectId));
      },
      error: (error) => {
        this.deleteErrorMessage.set(error?.error?.message || 'Unable to delete project.');
      }
    });
  }

  protected closeDeleteErrorModal(): void {
    this.deleteErrorMessage.set('');
  }

  protected closeDeleteConfirmModal(): void {
    this.pendingDeleteUserId.set(null);
    this.pendingDeleteProjectId.set(null);
  }
}

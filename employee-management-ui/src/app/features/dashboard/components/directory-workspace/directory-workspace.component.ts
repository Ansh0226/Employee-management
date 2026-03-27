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
}

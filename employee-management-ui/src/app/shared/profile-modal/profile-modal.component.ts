import { CommonModule } from '@angular/common';
import { Component, inject, input, output, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

import { AuthService } from '../../core/auth.service';
import { ProjectDetailRecord, ProjectRecord, UserRecord } from '../../core/models';
import { WorkflowService } from '../../core/workflow.service';
import { ImagePreviewModalComponent } from '../image-preview-modal/image-preview-modal.component';
import { ProjectDetailModalComponent } from '../project-detail-modal/project-detail-modal.component';

@Component({
  selector: 'app-profile-modal',
  imports: [CommonModule, FormsModule, ImagePreviewModalComponent, ProjectDetailModalComponent],
  templateUrl: './profile-modal.component.html',
  styleUrl: './profile-modal.component.scss'
})
export class ProfileModalComponent {
  private readonly auth = inject(AuthService);
  private readonly workflow = inject(WorkflowService);

  readonly profile = input<UserRecord | null>(null);
  readonly profileLabel = input('U');
  readonly close = output<void>();

  protected readonly passwordPattern = '^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$';
  protected readonly imagePreviewVisible = signal(false);
  protected readonly projects = signal<ProjectRecord[]>([]);
  protected readonly selectedProject = signal<ProjectDetailRecord | null>(null);
  protected readonly passwordForm = {
    currentPassword: '',
    newPassword: ''
  };

  constructor() {
    this.loadProjects();
  }

  protected onClose(): void {
    this.imagePreviewVisible.set(false);
    this.passwordForm.currentPassword = '';
    this.passwordForm.newPassword = '';
    this.close.emit();
  }

  protected savePassword(form: NgForm): void {
    if (form.invalid) {
      return;
    }

    this.auth.changePassword(this.passwordForm).subscribe({
      next: (response) => {
        this.auth.setNotice(response.message, 'success');
        this.onClose();
      },
      error: (error) => {
        this.auth.setNotice(this.auth.getErrorMessage(error), 'error');
      }
    });
  }

  protected previewProfileImage(): void {
    if (this.profile()?.profileImage) {
      this.imagePreviewVisible.set(true);
    }
  }

  protected closeImagePreview(): void {
    this.imagePreviewVisible.set(false);
  }

  protected openProject(projectId: number): void {
    this.workflow.getProjectDetail(projectId).subscribe({
      next: (response) => this.selectedProject.set(response.data)
    });
  }

  protected closeProject(): void {
    this.selectedProject.set(null);
  }

  protected onProfileImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      const profileImage = typeof reader.result === 'string' ? reader.result : '';

      this.auth.updateProfileImage({ profileImage }).subscribe({
        next: (response) => {
          this.auth.setCurrentProfile(response.data);
          this.auth.setNotice(response.message, 'success');
        },
        error: (error) => {
          this.auth.setNotice(this.auth.getErrorMessage(error), 'error');
        }
      });
    };
    reader.readAsDataURL(file);
  }

  private loadProjects(): void {
    this.workflow.getProfileProjects().subscribe({
      next: (response) => this.projects.set(response.data ?? [])
    });
  }
}

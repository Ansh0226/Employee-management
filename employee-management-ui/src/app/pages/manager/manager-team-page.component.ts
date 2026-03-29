import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { ProjectDetailRecord, ProjectRecord, UserRecord } from '../../core/models';
import { WorkflowService } from '../../core/workflow.service';
import { MANAGER_MENU_ITEMS } from '../../features/dashboard/dashboard-menu';
import { DashboardLayoutComponent } from '../../features/dashboard/components/dashboard-layout/dashboard-layout.component';
import { ProjectDetailModalComponent } from '../../shared/project-detail-modal/project-detail-modal.component';

@Component({
  selector: 'app-manager-team-page',
  imports: [CommonModule, FormsModule, DashboardLayoutComponent, ProjectDetailModalComponent],
  templateUrl: './manager-team-page.component.html',
  styleUrl: './manager-team-page.component.scss'
})
export class ManagerTeamPageComponent {
  private readonly workflow = inject(WorkflowService);
  protected readonly team = signal<UserRecord[]>([]);
  protected readonly selectedMember = signal<UserRecord | null>(null);
  protected readonly selectedProjects = signal<ProjectRecord[]>([]);
  protected readonly selectedProject = signal<ProjectDetailRecord | null>(null);
  protected readonly locationDraft = signal('');
  protected readonly savingLocation = signal(false);
  protected readonly notice = signal('');
  protected readonly noticeTone = signal<'success' | 'error'>('success');
  protected readonly menuItems = MANAGER_MENU_ITEMS;

  constructor() {
    this.refresh();
  }

  protected refresh(): void {
    this.workflow.getMyTeam().subscribe({ next: (response) => this.team.set(response.data ?? []) });
  }

  protected openMember(member: UserRecord): void {
    this.selectedMember.set(member);
    this.locationDraft.set(member.location || '');
    this.notice.set('');
    this.workflow.getProjectsForUser(member.id).subscribe({
      next: (response) => this.selectedProjects.set(response.data ?? [])
    });
  }

  protected closeMemberModal(): void {
    this.selectedMember.set(null);
    this.selectedProjects.set([]);
    this.notice.set('');
  }

  protected openProject(projectId: number): void {
    this.workflow.getProjectDetail(projectId).subscribe({
      next: (response) => this.selectedProject.set(response.data ?? null)
    });
  }

  protected closeProjectModal(): void {
    this.selectedProject.set(null);
  }

  protected saveLocation(): void {
    const member = this.selectedMember();
    const location = this.locationDraft().trim();

    if (!member || !location) {
      this.notice.set('Location is required.');
      this.noticeTone.set('error');
      return;
    }

    this.savingLocation.set(true);
    this.workflow.updateEmployeeLocation(member.id, location).subscribe({
      next: (response) => {
        this.notice.set(response.message);
        this.noticeTone.set('success');
        this.savingLocation.set(false);
        this.refresh();
        this.selectedMember.update((current) => current ? { ...current, location } : current);
      },
      error: () => {
        this.notice.set('Unable to update location.');
        this.noticeTone.set('error');
        this.savingLocation.set(false);
      }
    });
  }
}

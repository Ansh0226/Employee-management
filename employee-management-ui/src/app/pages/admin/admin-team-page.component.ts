import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../core/auth.service';
import { ProjectRecord, UserRecord } from '../../core/models';
import { WorkflowService } from '../../core/workflow.service';
import { DashboardLayoutComponent } from '../../features/dashboard/components/dashboard-layout/dashboard-layout.component';
import { DashboardStoreService } from '../../features/dashboard/services/dashboard-store.service';
import { ProjectDetailModalComponent } from '../../shared/project-detail-modal/project-detail-modal.component';

@Component({
  selector: 'app-admin-team-page',
  imports: [CommonModule, FormsModule, DashboardLayoutComponent, ProjectDetailModalComponent],
  providers: [DashboardStoreService],
  templateUrl: './admin-team-page.component.html',
  styleUrl: './admin-team-page.component.scss'
})
export class AdminTeamPageComponent {
  private readonly auth = inject(AuthService);
  private readonly workflow = inject(WorkflowService);
  protected readonly managers = signal<UserRecord[]>([]);
  protected readonly teamMembers = signal<UserRecord[]>([]);
  protected readonly managerProjects = signal<ProjectRecord[]>([]);
  protected readonly selectedManager = signal<UserRecord | null>(null);
  protected readonly selectedTeamMember = signal<UserRecord | null>(null);
  protected readonly selectedTeamMemberProjects = signal<ProjectRecord[]>([]);
  protected readonly selectedProject = signal<any | null>(null);
  protected readonly selectedManagerId = signal<number | null>(null);
  protected readonly assigning = signal(false);
  protected readonly form = { employeeId: 0, managerId: 0 };
  protected readonly menuItems = [
    { label: 'Directory', route: '/admin', note: 'Search, filter, export, and view employees.' },
    { label: 'Team', route: '/admin/team', note: 'Assign employees to managers.' },
    { label: 'Projects', route: '/admin/projects', note: 'Create projects and assign managers.' },
    { label: 'Approvals', route: '/admin/approvals', note: 'Approve newly registered users.' }
  ];
  protected readonly employees = computed(() =>
    this.store.users().filter((user) => user.role === 'EMPLOYEE')
  );

  constructor(protected readonly store: DashboardStoreService) {
    this.refresh();
  }

  protected refresh(): void {
    this.store.refresh();
    this.loadManagers();
  }

  protected assign(): void {
    if (!this.form.employeeId || !this.form.managerId) {
      this.store.notice.set('Select both employee and manager.');
      this.store.noticeTone.set('error');
      return;
    }

    this.assigning.set(true);
    this.workflow.assignEmployeeToManager(this.form).subscribe({
      next: (response) => {
        this.store.notice.set(response.message);
        this.store.noticeTone.set('success');
        this.assigning.set(false);
        this.refresh();
        if (this.selectedManagerId() === this.form.managerId) {
          this.loadTeam(this.form.managerId);
        }
      },
      error: (error) => {
        this.store.notice.set(this.auth.getErrorMessage(error));
        this.store.noticeTone.set('error');
        this.assigning.set(false);
      }
    });
  }

  protected chooseManager(managerId: number): void {
    this.selectedManagerId.set(managerId);
    this.form.managerId = managerId;
    this.selectedManager.set(this.managers().find((manager) => manager.id === managerId) ?? null);
    this.loadTeam(managerId);
    this.loadManagerProjects(managerId);
  }

  protected closeManagerModal(): void {
    this.selectedManager.set(null);
  }

  protected openTeamMember(member: UserRecord): void {
    this.selectedTeamMember.set(member);
    this.workflow.getProjectsForUser(member.id).subscribe({
      next: (response) => {
        this.selectedTeamMemberProjects.set(response.data ?? []);
      }
    });
  }

  protected closeTeamMemberModal(): void {
    this.selectedTeamMember.set(null);
    this.selectedTeamMemberProjects.set([]);
  }

  protected openProject(projectId: number): void {
    this.workflow.getProjectDetail(projectId).subscribe({
      next: (response) => {
        this.selectedProject.set(response.data ?? null);
      }
    });
  }

  protected closeProjectModal(): void {
    this.selectedProject.set(null);
  }

  private loadManagers(): void {
    this.workflow.getManagers().subscribe({
      next: (response) => {
        this.managers.set(response.data ?? []);
      }
    });
  }

  private loadTeam(managerId: number): void {
    this.workflow.getManagerTeam(managerId).subscribe({
      next: (response) => {
        this.teamMembers.set(response.data ?? []);
      }
    });
  }

  private loadManagerProjects(managerId: number): void {
    this.workflow.getProjectsForManager(managerId).subscribe({
      next: (response) => {
        this.managerProjects.set(response.data ?? []);
      }
    });
  }
}

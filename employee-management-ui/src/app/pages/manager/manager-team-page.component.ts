import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';

import { UserRecord } from '../../core/models';
import { WorkflowService } from '../../core/workflow.service';
import { DashboardLayoutComponent } from '../../features/dashboard/components/dashboard-layout/dashboard-layout.component';

@Component({
  selector: 'app-manager-team-page',
  imports: [CommonModule, DashboardLayoutComponent],
  templateUrl: './manager-team-page.component.html',
  styleUrl: './manager-team-page.component.scss'
})
export class ManagerTeamPageComponent {
  private readonly workflow = inject(WorkflowService);
  protected readonly team = signal<UserRecord[]>([]);
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
    this.workflow.getMyTeam().subscribe({ next: (response) => this.team.set(response.data ?? []) });
  }
}

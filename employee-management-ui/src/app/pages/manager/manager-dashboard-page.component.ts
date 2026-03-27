import { Component } from '@angular/core';

import { DirectoryWorkspaceComponent } from '../../features/dashboard/components/directory-workspace/directory-workspace.component';
import { DashboardLayoutComponent } from '../../features/dashboard/components/dashboard-layout/dashboard-layout.component';
import { DashboardStoreService } from '../../features/dashboard/services/dashboard-store.service';

@Component({
  selector: 'app-manager-dashboard-page',
  imports: [DashboardLayoutComponent, DirectoryWorkspaceComponent],
  providers: [DashboardStoreService],
  templateUrl: './manager-dashboard-page.component.html'
})
export class ManagerDashboardPageComponent {
  protected readonly menuItems = [
    { label: 'Directory', route: '/manager', note: 'See approved employees in your directory.' },
    { label: 'Team', route: '/manager/team', note: 'View your assigned team.' },
    { label: 'Projects', route: '/manager/projects', note: 'See your assigned projects.' },
    { label: 'Tasks', route: '/manager/tasks', note: 'Create tasks and approve completed work.' }
  ];

  constructor(protected readonly store: DashboardStoreService) {
    this.store.refresh();
  }
}

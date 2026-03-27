import { Component } from '@angular/core';

import { DirectoryWorkspaceComponent } from '../../features/dashboard/components/directory-workspace/directory-workspace.component';
import { DashboardLayoutComponent } from '../../features/dashboard/components/dashboard-layout/dashboard-layout.component';
import { DashboardStoreService } from '../../features/dashboard/services/dashboard-store.service';

@Component({
  selector: 'app-admin-roles-page',
  imports: [DashboardLayoutComponent, DirectoryWorkspaceComponent],
  providers: [DashboardStoreService],
  templateUrl: './admin-roles-page.component.html'
})
export class AdminRolesPageComponent {
  protected readonly menuItems = [
    { label: 'Directory', route: '/admin', note: 'Search, filter, export, and view employees.' },
    { label: 'Team', route: '/admin/team', note: 'Assign employees to managers.' },
    { label: 'Projects', route: '/admin/projects', note: 'Create projects and assign managers.' },
    { label: 'Approvals', route: '/admin/approvals', note: 'Approve newly registered users.' },
    { label: 'Roles', route: '/admin/roles', note: 'Change employee and manager roles.' }
  ];

  constructor(protected readonly store: DashboardStoreService) {
    this.store.refresh();
  }
}

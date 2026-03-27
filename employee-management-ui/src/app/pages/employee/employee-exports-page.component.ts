import { Component } from '@angular/core';

import { DirectoryWorkspaceComponent } from '../../features/dashboard/components/directory-workspace/directory-workspace.component';
import { DashboardLayoutComponent } from '../../features/dashboard/components/dashboard-layout/dashboard-layout.component';
import { DashboardStoreService } from '../../features/dashboard/services/dashboard-store.service';

@Component({
  selector: 'app-employee-exports-page',
  imports: [DashboardLayoutComponent, DirectoryWorkspaceComponent],
  providers: [DashboardStoreService],
  templateUrl: './employee-exports-page.component.html'
})
export class EmployeeExportsPageComponent {
  protected readonly menuItems = [
    { label: 'Directory', route: '/employee', note: 'Browse the employee directory.' },
    { label: 'Tasks', route: '/employee/tasks', note: 'See and complete your assigned tasks.' },
    { label: 'Exports', route: '/employee/exports', note: 'Download the directory in Excel or PDF.' }
  ];

  constructor(protected readonly store: DashboardStoreService) {
    this.store.refresh();
  }
}

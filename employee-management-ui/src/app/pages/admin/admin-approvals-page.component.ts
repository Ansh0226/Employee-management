import { Component } from '@angular/core';

import { DashboardLayoutComponent } from '../../features/dashboard/components/dashboard-layout/dashboard-layout.component';
import { PendingApprovalsPanelComponent } from '../../features/dashboard/components/pending-approvals-panel/pending-approvals-panel.component';
import { DashboardStoreService } from '../../features/dashboard/services/dashboard-store.service';

@Component({
  selector: 'app-admin-approvals-page',
  imports: [DashboardLayoutComponent, PendingApprovalsPanelComponent],
  providers: [DashboardStoreService],
  templateUrl: './admin-approvals-page.component.html'
})
export class AdminApprovalsPageComponent {
  protected readonly menuItems = [
    { label: 'Directory', route: '/admin', note: 'Search, filter, export, and view employees.' },
    { label: 'Team', route: '/admin/team', note: 'Assign employees to managers.' },
    { label: 'Projects', route: '/admin/projects', note: 'Create projects and assign managers.' },
    { label: 'Approvals', route: '/admin/approvals', note: 'Approve newly registered users.' }
  ];

  constructor(protected readonly store: DashboardStoreService) {
    this.store.refresh();
  }
}

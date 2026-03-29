import { Component } from '@angular/core';

import { ADMIN_MENU_ITEMS } from '../../features/dashboard/dashboard-menu';
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
  protected readonly menuItems = ADMIN_MENU_ITEMS;

  constructor(protected readonly store: DashboardStoreService) {
    this.store.refresh();
  }
}

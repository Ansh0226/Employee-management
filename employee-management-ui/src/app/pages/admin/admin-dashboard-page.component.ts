import { Component } from '@angular/core';

import { ADMIN_MENU_ITEMS } from '../../features/dashboard/dashboard-menu';
import { DirectoryWorkspaceComponent } from '../../features/dashboard/components/directory-workspace/directory-workspace.component';
import { DashboardLayoutComponent } from '../../features/dashboard/components/dashboard-layout/dashboard-layout.component';
import { DashboardStoreService } from '../../features/dashboard/services/dashboard-store.service';

@Component({
  selector: 'app-admin-dashboard-page',
  imports: [DashboardLayoutComponent, DirectoryWorkspaceComponent],
  providers: [DashboardStoreService],
  templateUrl: './admin-dashboard-page.component.html'
})
export class AdminDashboardPageComponent {
  protected readonly menuItems = ADMIN_MENU_ITEMS;

  constructor(protected readonly store: DashboardStoreService) {
    this.store.refresh();
  }
}

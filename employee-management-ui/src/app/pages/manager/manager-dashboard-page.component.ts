import { Component } from '@angular/core';

import { MANAGER_MENU_ITEMS } from '../../features/dashboard/dashboard-menu';
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
  protected readonly menuItems = MANAGER_MENU_ITEMS;

  constructor(protected readonly store: DashboardStoreService) {
    this.store.refresh();
  }
}

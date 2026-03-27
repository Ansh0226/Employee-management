import { Component, input } from '@angular/core';

import { NavbarComponent } from '../../../../shared/navbar/navbar.component';
import { DashboardSidebarComponent, SidebarItem } from '../dashboard-sidebar/dashboard-sidebar.component';

@Component({
  selector: 'app-dashboard-layout',
  imports: [NavbarComponent, DashboardSidebarComponent],
  templateUrl: './dashboard-layout.component.html',
  styleUrl: './dashboard-layout.component.scss'
})
export class DashboardLayoutComponent {
  readonly title = input.required<string>();
  readonly items = input.required<SidebarItem[]>();
  readonly activeItem = input.required<string>();
  readonly refreshHandler = input<(() => void) | null>(null);

  protected onRefresh(): void {
    this.refreshHandler()?.();
  }
}

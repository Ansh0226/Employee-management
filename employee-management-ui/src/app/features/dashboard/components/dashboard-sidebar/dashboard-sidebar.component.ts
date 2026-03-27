import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

export interface SidebarItem {
  label: string;
  route: string;
  note: string;
}

@Component({
  selector: 'app-dashboard-sidebar',
  imports: [RouterLink],
  templateUrl: './dashboard-sidebar.component.html',
  styleUrl: './dashboard-sidebar.component.scss'
})
export class DashboardSidebarComponent {
  readonly title = input.required<string>();
  readonly activeItem = input.required<string>();
  readonly items = input.required<SidebarItem[]>();
}

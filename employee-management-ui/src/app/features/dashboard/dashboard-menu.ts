import { SidebarItem } from './components/dashboard-sidebar/dashboard-sidebar.component';

export const ADMIN_MENU_ITEMS: SidebarItem[] = [
  { label: 'Directory', route: '/admin' },
  { label: 'Team', route: '/admin/team' },
  { label: 'Projects', route: '/admin/projects' },
  { label: 'Approvals', route: '/admin/approvals' }
];

export const MANAGER_MENU_ITEMS: SidebarItem[] = [
  { label: 'Directory', route: '/manager' },
  { label: 'Team', route: '/manager/team' },
  { label: 'Projects', route: '/manager/projects' },
  { label: 'Tasks', route: '/manager/tasks' }
];

export const EMPLOYEE_MENU_ITEMS: SidebarItem[] = [
  { label: 'Directory', route: '/employee' },
  { label: 'Tasks', route: '/employee/tasks' }
];

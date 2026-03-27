import { Routes } from '@angular/router';

import { authGuard, guestGuard, roleGuard } from './core/auth.guards';
import { AdminApprovalsPageComponent } from './pages/admin/admin-approvals-page.component';
import { AdminDashboardPageComponent } from './pages/admin/admin-dashboard-page.component';
import { AdminProjectsPageComponent } from './pages/admin/admin-projects-page.component';
import { AdminTeamPageComponent } from './pages/admin/admin-team-page.component';
import { EmployeeDashboardPageComponent } from './pages/employee/employee-dashboard-page.component';
import { EmployeeTasksPageComponent } from './pages/employee/employee-tasks-page.component';
import { LoginPageComponent } from './pages/login/login-page.component';
import { ManagerDashboardPageComponent } from './pages/manager/manager-dashboard-page.component';
import { ManagerProjectsPageComponent } from './pages/manager/manager-projects-page.component';
import { ManagerTasksPageComponent } from './pages/manager/manager-tasks-page.component';
import { ManagerTeamPageComponent } from './pages/manager/manager-team-page.component';
import { RedirectPageComponent } from './pages/redirect/redirect-page.component';
import { RegisterPageComponent } from './pages/register/register-page.component';

export const routes: Routes = [
  {
    path: '',
    component: RedirectPageComponent
  },
  {
    path: 'login',
    component: LoginPageComponent,
    canActivate: [guestGuard]
  },
  {
    path: 'register',
    component: RegisterPageComponent,
    canActivate: [guestGuard]
  },
  {
    path: 'admin',
    component: AdminDashboardPageComponent,
    canActivate: [authGuard, roleGuard('ADMIN')]
  },
  {
    path: 'admin/team',
    component: AdminTeamPageComponent,
    canActivate: [authGuard, roleGuard('ADMIN')]
  },
  {
    path: 'admin/projects',
    component: AdminProjectsPageComponent,
    canActivate: [authGuard, roleGuard('ADMIN')]
  },
  {
    path: 'admin/approvals',
    component: AdminApprovalsPageComponent,
    canActivate: [authGuard, roleGuard('ADMIN')]
  },
  {
    path: 'manager',
    component: ManagerDashboardPageComponent,
    canActivate: [authGuard, roleGuard('MANAGER')]
  },
  {
    path: 'manager/team',
    component: ManagerTeamPageComponent,
    canActivate: [authGuard, roleGuard('MANAGER')]
  },
  {
    path: 'manager/projects',
    component: ManagerProjectsPageComponent,
    canActivate: [authGuard, roleGuard('MANAGER')]
  },
  {
    path: 'manager/tasks',
    component: ManagerTasksPageComponent,
    canActivate: [authGuard, roleGuard('MANAGER')]
  },
  {
    path: 'employee',
    component: EmployeeDashboardPageComponent,
    canActivate: [authGuard, roleGuard('EMPLOYEE')]
  },
  {
    path: 'employee/tasks',
    component: EmployeeTasksPageComponent,
    canActivate: [authGuard, roleGuard('EMPLOYEE')]
  },
  {
    path: '**',
    redirectTo: ''
  }
];

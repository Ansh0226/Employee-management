import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { AuthService } from '../../../core/auth.service';
import { ApiResponse, PageResponse, Role, UserRecord } from '../../../core/models';
import { WorkflowService } from '../../../core/workflow.service';

type ExportFormat = 'excel' | 'pdf';
type NoticeTone = 'success' | 'error' | 'info';

@Injectable()
export class DashboardStoreService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);
  private readonly workflow = inject(WorkflowService);
  private readonly apiBase = 'http://localhost:8080';

  readonly directoryBusy = signal(false);
  readonly adminBusy = signal(false);
  readonly exportBusy = signal(false);
  readonly notice = signal('');
  readonly noticeTone = signal<NoticeTone>('info');
  readonly users = signal<UserRecord[]>([]);
  readonly adminUsers = signal<UserRecord[]>([]);
  readonly selectedUser = signal<UserRecord | null>(null);
  readonly pageNumber = signal(0);
  readonly pageSize = signal(5);
  readonly totalPages = signal(0);
  readonly filters = signal({
    keyword: '',
    location: '',
    minAge: null as number | null,
    maxAge: null as number | null,
    sortBy: 'firstName',
    direction: 'asc'
  });
  readonly roleDrafts = signal<Record<number, Role>>({});
  readonly locationDrafts = signal<Record<number, string>>({});
  readonly managerDrafts = signal<Record<number, number | null>>({});

  readonly currentRole = this.auth.currentRole;
  readonly isAdmin = computed(() => this.currentRole() === 'ADMIN');
  readonly isManager = computed(() => this.currentRole() === 'MANAGER');
  readonly visibleUsers = computed(() => {
    const baseUsers = this.users().filter((user) => user.role !== 'ADMIN');

    if (this.isManager()) {
      return baseUsers.filter((user) => user.role === 'EMPLOYEE');
    }

    return baseUsers;
  });
  readonly pendingApprovals = computed(() =>
    this.adminUsers().filter((user) => user.status === 'PENDING' && user.role !== 'ADMIN')
  );
  readonly managerUsers = computed(() =>
    this.adminUsers().filter((user) => user.role === 'MANAGER')
  );
  readonly hasAgeFilter = computed(() => {
    const filters = this.filters();
    return filters.minAge !== null && filters.maxAge !== null;
  });

  refresh(): void {
    this.loadDirectory();

    if (this.isAdmin()) {
      this.loadAdminUsers();
    }
  }

  applyFilters(): void {
    this.pageNumber.set(0);
    this.loadDirectory();
  }

  resetFilters(): void {
    this.filters.set({
      keyword: '',
      location: '',
      minAge: null,
      maxAge: null,
      sortBy: 'firstName',
      direction: 'asc'
    });
    this.pageNumber.set(0);
    this.loadDirectory();
  }

  updateFilter<K extends keyof ReturnType<DashboardStoreService['filters']>>(key: K, value: ReturnType<DashboardStoreService['filters']>[K]): void {
    this.filters.update((filters) => ({ ...filters, [key]: value }));
  }

  chooseUser(user: UserRecord): void {
    this.selectedUser.set(user);
    this.locationDrafts.update((drafts) => ({ ...drafts, [user.id]: user.location ?? '' }));
    this.roleDrafts.update((drafts) => ({ ...drafts, [user.id]: user.role }));
    this.managerDrafts.update((drafts) => ({ ...drafts, [user.id]: user.managerId ?? null }));
  }

  updateRoleDraft(userId: number, role: Role): void {
    this.roleDrafts.update((drafts) => ({ ...drafts, [userId]: role }));
  }

  updateLocationDraft(userId: number, location: string): void {
    this.locationDrafts.update((drafts) => ({ ...drafts, [userId]: location }));
  }

  updateManagerDraft(userId: number, managerId: number | null): void {
    this.managerDrafts.update((drafts) => ({ ...drafts, [userId]: managerId }));
  }

  approveUser(userId: number): void {
    this.adminBusy.set(true);

    this.http
      .put<ApiResponse<UserRecord>>(`${this.apiBase}/admin/approve/${userId}`, {}, { headers: this.authHeaders() })
      .subscribe({
        next: (response) => {
          this.showNotice(response.message, 'success');
          this.refresh();
          this.adminBusy.set(false);
        },
        error: (error) => {
          this.showNotice(this.auth.getErrorMessage(error), 'error');
          this.adminBusy.set(false);
        }
      });
  }

  changeRole(): void {
    const user = this.selectedUser();

    if (!user) {
      return;
    }

    const nextRole = this.roleDrafts()[user.id] ?? user.role;
    this.adminBusy.set(true);

    this.http
      .put<ApiResponse<UserRecord>>(
        `${this.apiBase}/admin/change-role/${user.id}?role=${nextRole}`,
        {},
        { headers: this.authHeaders() }
      )
      .subscribe({
        next: (response) => {
          this.showNotice(response.message, 'success');
          this.refresh();
          this.adminBusy.set(false);
        },
        error: (error) => {
          this.showNotice(this.auth.getErrorMessage(error), 'error');
          this.adminBusy.set(false);
        }
      });
  }

  saveAdminUser(): void {
    const user = this.selectedUser();

    if (!user) {
      return;
    }

    const nextRole = this.roleDrafts()[user.id] ?? user.role;
    const nextManagerId = this.managerDrafts()[user.id] ?? user.managerId ?? null;
    this.adminBusy.set(true);

    this.http
      .put<ApiResponse<UserRecord>>(
        `${this.apiBase}/admin/change-role/${user.id}?role=${nextRole}`,
        {},
        { headers: this.authHeaders() }
      )
      .subscribe({
        next: () => {
          if (nextRole === 'EMPLOYEE' && nextManagerId) {
            this.workflow.assignEmployeeToManager({ employeeId: user.id, managerId: nextManagerId }).subscribe({
              next: (response) => {
                this.showNotice(response.message, 'success');
                this.refresh();
                this.adminBusy.set(false);
              },
              error: (error) => {
                this.showNotice(this.auth.getErrorMessage(error), 'error');
                this.adminBusy.set(false);
              }
            });
            return;
          }

          this.showNotice('User updated successfully', 'success');
          this.refresh();
          this.adminBusy.set(false);
        },
        error: (error) => {
          this.showNotice(this.auth.getErrorMessage(error), 'error');
          this.adminBusy.set(false);
        }
      });
  }

  approveUserWithRole(userId: number, role: Role, managerId: number | null): void {
    this.adminBusy.set(true);

    this.http
      .put<ApiResponse<UserRecord>>(`${this.apiBase}/admin/approve/${userId}`, {}, { headers: this.authHeaders() })
      .subscribe({
        next: () => {
          this.http
            .put<ApiResponse<UserRecord>>(
              `${this.apiBase}/admin/change-role/${userId}?role=${role}`,
              {},
              { headers: this.authHeaders() }
            )
            .subscribe({
              next: () => {
                if (role === 'EMPLOYEE' && managerId) {
                  this.workflow.assignEmployeeToManager({ employeeId: userId, managerId }).subscribe({
                    next: (response) => {
                      this.showNotice(response.message, 'success');
                      this.refresh();
                      this.adminBusy.set(false);
                    },
                    error: (error) => {
                      this.showNotice(this.auth.getErrorMessage(error), 'error');
                      this.adminBusy.set(false);
                    }
                  });
                  return;
                }

                this.showNotice('User approved successfully', 'success');
                this.refresh();
                this.adminBusy.set(false);
              },
              error: (error) => {
                this.showNotice(this.auth.getErrorMessage(error), 'error');
                this.adminBusy.set(false);
              }
            });
        },
        error: (error) => {
          this.showNotice(this.auth.getErrorMessage(error), 'error');
          this.adminBusy.set(false);
        }
      });
  }

  saveManagerLocation(): void {
    const user = this.selectedUser();

    if (!user || user.role !== 'EMPLOYEE') {
      this.showNotice('Managers can update employee location only.', 'error');
      return;
    }

    const nextLocation = (this.locationDrafts()[user.id] ?? '').trim();

    if (!nextLocation) {
      this.showNotice('Location is required.', 'error');
      return;
    }

    this.directoryBusy.set(true);

    this.http
      .put<ApiResponse<UserRecord>>(
        `${this.apiBase}/manager/update-location/${user.id}?location=${encodeURIComponent(nextLocation)}`,
        {},
        { headers: this.authHeaders() }
      )
      .subscribe({
        next: (response) => {
          this.showNotice(response.message, 'success');
          this.loadDirectory();
          this.directoryBusy.set(false);
        },
        error: (error) => {
          this.showNotice(this.auth.getErrorMessage(error), 'error');
          this.directoryBusy.set(false);
        }
      });
  }

  exportDirectory(format: ExportFormat): void {
    this.exportBusy.set(true);

    this.http
      .get(`${this.apiBase}/exports/${format}${this.exportQuery()}`, {
        headers: this.authHeaders(),
        responseType: 'blob'
      })
      .subscribe({
        next: (file) => {
          const url = window.URL.createObjectURL(file);
          const link = document.createElement('a');
          link.href = url;
          link.download = `directory-${this.currentRole()?.toLowerCase()}.${format === 'excel' ? 'xlsx' : 'pdf'}`;
          link.click();
          window.URL.revokeObjectURL(url);
          this.exportBusy.set(false);
        },
        error: (error) => {
          this.showNotice(this.auth.getErrorMessage(error), 'error');
          this.exportBusy.set(false);
        }
      });
  }

  goToPreviousPage(): void {
    if (this.pageNumber() === 0) {
      return;
    }

    this.pageNumber.update((page) => page - 1);
    this.loadDirectory();
  }

  goToNextPage(): void {
    if (this.pageNumber() + 1 >= this.totalPages()) {
      return;
    }

    this.pageNumber.update((page) => page + 1);
    this.loadDirectory();
  }

  trackByUserId(_: number, user: UserRecord): number {
    return user.id;
  }

  selectedUserLabel(): string {
    const user = this.selectedUser();
    return (user?.firstName?.trim().charAt(0) || user?.username?.trim().charAt(0) || 'U').toUpperCase();
  }

  isSelected(user: UserRecord): boolean {
    return this.selectedUser()?.id === user.id;
  }

  private loadDirectory(): void {
    this.directoryBusy.set(true);

    const page = this.pageNumber();
    const size = this.pageSize();
    const filters = this.filters();
    const keyword = filters.keyword.trim();
    const location = filters.location.trim();
    const canUseAgeFilter = this.hasAgeFilter();

    let endpoint = '';

    if (keyword) {
      endpoint = this.isManager()
        ? `/manager/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}&sortBy=${filters.sortBy}&direction=${filters.direction}`
        : `/employee/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`;
    } else if (location) {
      endpoint = this.isManager()
        ? `/manager/filter/location?location=${encodeURIComponent(location)}&page=${page}&size=${size}&sortBy=${filters.sortBy}&direction=${filters.direction}`
        : `/employee/filter/location?location=${encodeURIComponent(location)}&page=${page}&size=${size}`;
    } else if (canUseAgeFilter) {
      endpoint = this.isManager()
        ? `/manager/filter/age-range?minAge=${filters.minAge}&maxAge=${filters.maxAge}&page=${page}&size=${size}&sortBy=${filters.sortBy}&direction=${filters.direction}`
        : `/employee/filter/age-range?minAge=${filters.minAge}&maxAge=${filters.maxAge}&page=${page}&size=${size}`;
    } else {
      endpoint = this.isManager()
        ? `/manager/employees?page=${page}&size=${size}&sortBy=${filters.sortBy}&direction=${filters.direction}`
        : `/employee?page=${page}&size=${size}&sortBy=${filters.sortBy}&direction=${filters.direction}`;
    }

    this.http
      .get<ApiResponse<PageResponse<UserRecord>>>(`${this.apiBase}${endpoint}`, { headers: this.authHeaders() })
      .subscribe({
        next: (response) => {
          const pageData = response.data;
          this.users.set(pageData.content ?? []);
          this.totalPages.set(pageData.totalPages ?? 0);
          this.syncSelectedUser();
          this.directoryBusy.set(false);
        },
        error: (error) => {
          this.showNotice(this.auth.getErrorMessage(error), 'error');
          this.directoryBusy.set(false);
        }
      });
  }

  private loadAdminUsers(): void {
    this.adminBusy.set(true);

    this.http
      .get<ApiResponse<UserRecord[]>>(`${this.apiBase}/admin/users`, { headers: this.authHeaders() })
      .subscribe({
        next: (response) => {
          this.adminUsers.set(response.data ?? []);
          this.adminBusy.set(false);
        },
        error: (error) => {
          this.showNotice(this.auth.getErrorMessage(error), 'error');
          this.adminBusy.set(false);
        }
      });
  }

  private syncSelectedUser(): void {
    const list = this.visibleUsers();

    if (list.length === 0) {
      this.selectedUser.set(null);
      return;
    }

    const current = this.selectedUser();
    const updated = current ? list.find((user) => user.id === current.id) : null;

    if (!updated) {
      this.selectedUser.set(null);
      return;
    }

    this.selectedUser.set(updated);
    this.locationDrafts.update((drafts) => ({ ...drafts, [updated.id]: updated.location ?? '' }));
    this.roleDrafts.update((drafts) => ({ ...drafts, [updated.id]: updated.role }));
    this.managerDrafts.update((drafts) => ({ ...drafts, [updated.id]: updated.managerId ?? null }));
  }

  private exportQuery(): string {
    const filters = this.filters();
    const params = new URLSearchParams({
      viewRole: this.currentRole() ?? 'EMPLOYEE',
      keyword: filters.keyword,
      location: filters.location,
      sortBy: filters.sortBy,
      direction: filters.direction
    });

    if (filters.minAge !== null) {
      params.set('minAge', String(filters.minAge));
    }

    if (filters.maxAge !== null) {
      params.set('maxAge', String(filters.maxAge));
    }

    return `?${params.toString()}`;
  }

  private authHeaders(): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${this.auth.token()}`
    });
  }

  private showNotice(message: string, tone: NoticeTone): void {
    this.notice.set(message);
    this.noticeTone.set(tone);
    if (typeof window !== 'undefined') {
      window.setTimeout(() => {
        if (this.notice() === message) {
          this.notice.set('');
          this.noticeTone.set('info');
        }
      }, 3000);
    }
  }
}

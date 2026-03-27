import { CommonModule } from '@angular/common';
import { Component, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Role } from '../../../../core/models';

import { DashboardStoreService } from '../../services/dashboard-store.service';

@Component({
  selector: 'app-pending-approvals-panel',
  imports: [CommonModule, FormsModule],
  templateUrl: './pending-approvals-panel.component.html',
  styleUrl: './pending-approvals-panel.component.scss'
})
export class PendingApprovalsPanelComponent {
  readonly store = input.required<DashboardStoreService>();
  protected readonly selectedUserId = signal<number | null>(null);
  protected readonly roleDraft = signal<Role>('EMPLOYEE');
  protected readonly managerDraft = signal<number | null>(null);

  protected openUserModal(userId: number): void {
    const user = this.store().pendingApprovals().find((item) => item.id === userId);
    this.selectedUserId.set(userId);
    this.roleDraft.set('EMPLOYEE');
    this.managerDraft.set(user?.managerId ?? null);
  }

  protected closeUserModal(): void {
    this.selectedUserId.set(null);
  }

  protected selectedUser() {
    return this.store().pendingApprovals().find((item) => item.id === this.selectedUserId()) ?? null;
  }

  protected approveSelectedUser(): void {
    const user = this.selectedUser();
    if (!user) {
      return;
    }

    this.store().approveUserWithRole(user.id, this.roleDraft(), this.roleDraft() === 'EMPLOYEE' ? this.managerDraft() : null);
    this.closeUserModal();
  }
}

import { CommonModule } from '@angular/common';
import { Component, computed, inject, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { ThemeService } from '../../core/theme.service';
import { ProfileModalComponent } from '../profile-modal/profile-modal.component';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterLink, ProfileModalComponent],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  private readonly auth = inject(AuthService);
  protected readonly theme = inject(ThemeService);

  readonly activePage = input<'login' | 'register' | null>(null);
  readonly showRefresh = input(false);
  readonly refresh = output<void>();

  protected readonly isAuthenticated = this.auth.isAuthenticated;
  protected readonly currentUsername = this.auth.currentUsername;
  protected readonly currentProfile = this.auth.currentProfile;
  protected readonly profileLabel = computed(() =>
    (this.currentProfile()?.firstName?.trim().charAt(0)
      || this.currentUsername().trim().charAt(0)
      || 'U').toUpperCase()
  );
  protected profileVisible = false;

  protected logout(): void {
    this.auth.logout();
  }

  protected onRefresh(): void {
    this.refresh.emit();
  }

  protected openProfile(): void {
    this.auth.loadProfile();
    this.profileVisible = true;
  }

  protected closeProfile(): void {
    this.profileVisible = false;
  }
}

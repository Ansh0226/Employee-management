import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';

import { ApiResponse, ChangePasswordRequest, LoginRequest, NoticeTone, Role, SignupRequest, UpdateProfileContactRequest, UpdateProfileImageRequest, UserRecord } from './models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly isBrowser = isPlatformBrowser(this.platformId);
  private readonly apiBase = 'http://localhost:8080';

  readonly token = signal('');
  readonly currentRole = signal<Role | null>(null);
  readonly currentUsername = signal('');
  readonly currentProfile = signal<UserRecord | null>(null);
  readonly authNotice = signal('');
  readonly authNoticeTone = signal<NoticeTone>('info');
  readonly isAuthenticated = computed(() => this.token().length > 0);

  constructor() {
    if (!this.isBrowser) {
      return;
    }

    const savedToken = window.localStorage.getItem('ems-token');

    if (savedToken) {
      this.applySession(savedToken, false);
    }
  }

  login(payload: LoginRequest) {
    return this.http.post<ApiResponse<string>>(`${this.apiBase}/auth/login`, payload);
  }

  signup(payload: SignupRequest) {
    return this.http.post(`${this.apiBase}/auth/signup`, payload);
  }

  fetchProfile() {
    return this.http.get<ApiResponse<UserRecord>>(`${this.apiBase}/employee/profile`, {
      headers: this.authHeaders()
    });
  }

  changePassword(payload: ChangePasswordRequest) {
    return this.http.put<ApiResponse<string>>(`${this.apiBase}/employee/change-password`, payload, {
      headers: this.authHeaders()
    });
  }

  updateProfileImage(payload: UpdateProfileImageRequest) {
    return this.http.put<ApiResponse<UserRecord>>(`${this.apiBase}/employee/profile-image`, payload, {
      headers: this.authHeaders()
    });
  }

  updateProfileContact(payload: UpdateProfileContactRequest) {
    return this.http.put<ApiResponse<UserRecord>>(`${this.apiBase}/employee/profile-contact`, payload, {
      headers: this.authHeaders()
    });
  }

  completeLogin(token: string): void {
    this.applySession(token, true);
    this.clearNotice();
    void this.router.navigateByUrl(this.dashboardUrl());
  }

  logout(): void {
    this.token.set('');
    this.currentRole.set(null);
    this.currentUsername.set('');
    this.currentProfile.set(null);
    this.clearNotice();

    if (this.isBrowser) {
      window.localStorage.removeItem('ems-token');
    }

    void this.router.navigate(['/login']);
  }

  loadProfile(): void {
    this.fetchProfile().subscribe({
      next: (response) => {
        this.currentProfile.set(response.data);
      },
      error: () => {
        this.currentProfile.set(null);
      }
    });
  }

  setCurrentProfile(profile: UserRecord): void {
    this.currentProfile.set(profile);
  }

  dashboardUrl(): string {
    const role = this.currentRole();

    if (role === 'ADMIN') {
      return '/admin';
    }

    if (role === 'MANAGER') {
      return '/manager';
    }

    return '/employee';
  }

  setNotice(message: string, tone: NoticeTone): void {
    this.authNotice.set(message);
    this.authNoticeTone.set(tone);
  }

  clearNotice(): void {
    this.authNotice.set('');
    this.authNoticeTone.set('info');
  }

  getErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      const body = error.error;

      if (typeof body === 'string' && body.trim()) {
        return body;
      }

      if (body?.message) {
        return body.message;
      }

      if (body?.errors) {
        return Object.values(body.errors).join(', ');
      }
    }

    return 'Something went wrong while contacting the server.';
  }

  private applySession(token: string, persist: boolean): void {
    this.token.set(token);

    const payload = this.decodeToken(token);
    const role = payload['role'] as Role | undefined;
    const username = payload['sub'] as string | undefined;

    this.currentRole.set(role ?? null);
    this.currentUsername.set(username ?? '');
    this.loadProfile();

    if (persist && this.isBrowser) {
      window.localStorage.setItem('ems-token', token);
    }
  }

  private decodeToken(token: string): Record<string, unknown> {
    try {
      const payload = token.split('.')[1];

      if (!payload || !this.isBrowser) {
        return {};
      }

      const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(window.atob(normalized));
    } catch {
      return {};
    }
  }

  private authHeaders() {
    return {
      Authorization: `Bearer ${this.token()}`
    };
  }
}

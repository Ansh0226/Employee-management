import { DOCUMENT, isPlatformBrowser } from '@angular/common';
import { Injectable, PLATFORM_ID, computed, inject, signal } from '@angular/core';

export type ThemeMode = 'light' | 'dark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly document = inject(DOCUMENT);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly isBrowser = isPlatformBrowser(this.platformId);

  readonly theme = signal<ThemeMode>('light');
  readonly isDark = computed(() => this.theme() === 'dark');

  constructor() {
    const savedTheme =
      this.isBrowser ? (window.localStorage.getItem('ems-theme') as ThemeMode | null) : null;

    this.setTheme(savedTheme === 'dark' ? 'dark' : 'light');
  }

  toggleTheme(): void {
    this.setTheme(this.isDark() ? 'light' : 'dark');
  }

  private setTheme(theme: ThemeMode): void {
    this.theme.set(theme);
    this.document.documentElement.setAttribute('data-theme', theme);

    if (this.isBrowser) {
      window.localStorage.setItem('ems-theme', theme);
    }
  }
}

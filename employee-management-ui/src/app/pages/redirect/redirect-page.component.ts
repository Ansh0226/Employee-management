import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-redirect-page',
  template: ''
})
export class RedirectPageComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  constructor() {
    void this.router.navigateByUrl(this.auth.isAuthenticated() ? this.auth.dashboardUrl() : '/login');
  }
}

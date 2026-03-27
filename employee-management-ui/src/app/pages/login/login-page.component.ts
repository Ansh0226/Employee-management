import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

import { AuthService } from '../../core/auth.service';
import { Role } from '../../core/models';
import { NavbarComponent } from '../../shared/navbar/navbar.component';

@Component({
  selector: 'app-login-page',
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.scss'
})
export class LoginPageComponent {
  private readonly auth = inject(AuthService);

  protected readonly busy = signal(false);
  protected readonly submitted = signal(false);
  protected readonly roles: Role[] = ['ADMIN', 'MANAGER', 'EMPLOYEE'];
  protected readonly loginForm = {
    identifier: '',
    password: '',
    role: 'ADMIN' as Role
  };
  protected readonly notice = this.auth.authNotice;
  protected readonly noticeTone = this.auth.authNoticeTone;

  protected login(form: NgForm): void {
    this.submitted.set(true);

    if (form.invalid) {
      return;
    }

    this.busy.set(true);

    this.auth.login(this.loginForm).subscribe({
      next: (response) => {
        this.auth.completeLogin(response.data);
        this.busy.set(false);
      },
      error: (error) => {
        this.auth.setNotice(this.auth.getErrorMessage(error), 'error');
        this.busy.set(false);
      }
    });
  }
}

import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { NavbarComponent } from '../../shared/navbar/navbar.component';

@Component({
  selector: 'app-register-page',
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './register-page.component.html',
  styleUrl: './register-page.component.scss'
})
export class RegisterPageComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly busy = signal(false);
  protected readonly submitted = signal(false);
  protected readonly notice = this.auth.authNotice;
  protected readonly noticeTone = this.auth.authNoticeTone;
  protected readonly usernamePattern = '^(?=.{3,20}$)(?![._])(?!.*[._]{2})[a-z0-9._]+(?<![._])$';
  protected readonly passwordPattern = '^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$';
  protected readonly contactPattern = '^[6-9]\\d{9}$';
  protected readonly signupForm = {
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    dob: '',
    contactNumber: '',
    location: '',
    profileImage: ''
  };

  protected register(form: NgForm): void {
    this.submitted.set(true);

    if (form.invalid) {
      return;
    }

    this.busy.set(true);

    this.auth.signup(this.signupForm).subscribe({
      next: () => {
        this.auth.setNotice('Registration successful. Please login after admin approval.', 'success');
        void this.router.navigate(['/login']);
        this.busy.set(false);
      },
      error: (error) => {
        this.auth.setNotice(this.auth.getErrorMessage(error), 'error');
        this.busy.set(false);
      }
    });
  }

  protected onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      this.signupForm.profileImage = '';
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      this.signupForm.profileImage = typeof reader.result === 'string' ? reader.result : '';
    };
    reader.readAsDataURL(file);
  }
}

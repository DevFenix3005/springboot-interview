import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {Router} from '@angular/router';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';

import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';

import {AuthService} from '../../services/auth.service';
import {LoginRequest} from '../../models/login-request';

@Component({
  selector: 'app-login',
  imports: [MatFormFieldModule, MatIconModule, MatInputModule, MatButtonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Login {

  private readonly snackBar = inject(MatSnackBar);
  private readonly authService = inject(AuthService)
  private readonly router = inject(Router);


  loginFormGroup = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required)
  })

  protected onSubmit($event: any) {
    if (this.loginFormGroup.valid) {
      const loginRequest = this.loginFormGroup.value as LoginRequest;
      this.authService.login(loginRequest).subscribe({
        next: () => {
          this.router.navigate(['/tasks']);
        },
        error: () => {
          this.snackBar.open("Invalid username or password", 'Close', {duration: 3000});
        },
        complete: () => {
          console.log('login complete');
        }
      })
    }
  }
}

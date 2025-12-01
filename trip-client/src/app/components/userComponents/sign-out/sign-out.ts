
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { UserService } from '../../../service/user.service';
import { MaterialModule } from '../../../material.module';
@Component({
  selector: 'app-sign-out',
  standalone: true,
  imports: [CommonModule, RouterModule,MaterialModule],
  templateUrl: './sign-out.html',
  styleUrl: './sign-out.css',
})
export class SignOutComponent {
  public loading = false;
  public error: string | null = null;

  constructor(private _userService: UserService, private router: Router) {}

  signOut() {
    this.error = null;
    this.loading = true;

    try {
    } catch (e) {
      console.warn('Client logout failed', e);
    }

    this._userService.signOut().subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/welcome']);
      },
      error: (err) => {
        console.warn('Server logout failed', err);
        this.loading = false;
        this.router.navigate(['/welcome']);
      }
    });
  }

  cancel() {
    this.router.navigate(['home']);
  }
}

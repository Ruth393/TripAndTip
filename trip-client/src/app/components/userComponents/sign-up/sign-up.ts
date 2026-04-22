// src/app/components/sign-up/sign-up.component.ts

import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { SignUp } from '../../../models/user.model';
import { UserService } from '../../../service/user.service';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from '../../../material.module';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [CommonModule, FormsModule, MaterialModule],
  templateUrl: './sign-up.html',
  styleUrl: './sign-up.css',
})
export class SignUpComponent {
  
  defaultImageUrl = 'assets/A1.png';       
  imagePreview: string = this.defaultImageUrl;     
  selectedFile: File | null = null;     

  toSignUp: SignUp = {
    userName: '',
    email: '',
    password: ''
  };

  signUpsSuccess = false;
  alreadyExists = false;
  isLoading = false;

  constructor(private _userService: UserService, private router: Router) {}

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    
    if (!file) {
      this.selectedFile = null;
      this.imagePreview = this.defaultImageUrl;
      return;
    }

    this.selectedFile = file;

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result as string;
    };
    reader.readAsDataURL(file);

    event.target.value = null
;
  }

  async signUp() {
    if (!this.toSignUp.userName || !this.toSignUp.email || !this.toSignUp.password) {
      return;
    }

    this.isLoading = true;

    const formData = new FormData();

    const userJson = {
      userName: this.toSignUp.userName,
      email: this.toSignUp.email,
      password: this.toSignUp.password
    };
    formData.append('user', new Blob([JSON.stringify(userJson)], { type: 'application/json' }));

    if (this.selectedFile) {
      formData.append('image', this.selectedFile, this.selectedFile.name);
    } else {
      try {
        const response = await fetch(this.defaultImageUrl);
        if (response.ok) {
          const defaultImageBlob = await response.blob();
          formData.append('image', defaultImageBlob, 'default_avatar.png');
        } else {
            const emptyFile = new Blob([''], { type: 'image/png' });
            formData.append('image', emptyFile, 'placeholder.png');
        }
      } catch (e) {
          const emptyFile = new Blob([''], { type: 'image/png' });
          formData.append('image', emptyFile, 'placeholder.png');
      }
    }

    this._userService.signUp(formData).subscribe({
      next: (res) => {
        this.signUpsSuccess = true;
        this.isLoading = false;
        this.router.navigate(['/list-trips']);
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 409) {
          this.alreadyExists = true;
          setTimeout(() => location.reload(), 1500);
        }
      }
    });
  }
}
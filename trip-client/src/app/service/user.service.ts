// src/app/service/user.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SignIn, SignUp, AuthResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/User';

  constructor(private http: HttpClient) {}

  // התחברות
  signIn(signIn: SignIn): Observable<SignIn> {
    return this.http.post<SignIn>(`${this.apiUrl}/signIn`, signIn);
  }

  // הרשמה
  signUp(signUp: SignUp): Observable<SignUp> {
    return this.http.post<SignUp>(`${this.apiUrl}/signUp`, signUp);
  }

  // קבלת הטוקן
  getToken(): string | null {
    const user = localStorage.getItem('user');
    if (user) {
      const userData = JSON.parse(user);
      return userData.token;
    }
    return null;
  }

  // בדיקה אם מחובר
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // יציאה
  logout(): void {
    localStorage.removeItem('user');
  }
}
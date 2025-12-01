import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SignIn, SignUp, AuthResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/user';

  constructor(private http: HttpClient) {
  }


  signIn(signIn: SignIn): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/signIn`, signIn, { withCredentials: true })

  }

  signUp(signUp: SignUp): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/signUp`, signUp, { withCredentials: true })
  }

  getCurrentUser(): Observable<AuthResponse> {
    return this.http.get<AuthResponse>(`${this.apiUrl}/me`, { withCredentials: true });
  }

  isSignedIn(): Observable<AuthResponse> {
    return this.getCurrentUser();
  }

  signOut(): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/signOut`, {}, { withCredentials: true })
  }
}
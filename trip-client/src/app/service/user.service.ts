// user.service.ts (השם הכי נכון – כי זה גם auth וגם user)
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { SignIn, SignUp, AuthResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/user';

  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  private authToken: string | null = null;

  constructor(private http: HttpClient) {
    this.authToken = typeof window !== 'undefined' ? window.localStorage.getItem('authToken') : null;
    this.loadCurrentUser().subscribe();
  }

  private setAuthToken(token: string | null): void {
    this.authToken = token;
    if (typeof window !== 'undefined') {
      if (token) {
        window.localStorage.setItem('authToken', token);
      } else {
        window.localStorage.removeItem('authToken');
      }
    }
  }

  private getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({
      Authorization: this.authToken ? `Bearer ${this.authToken}` : ''
    });
  }

  signIn(signIn: SignIn): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/signIn`, signIn, { withCredentials: true })
      .pipe(
        tap(user => {
          this.setAuthToken(user.token ?? null);
          this.currentUserSubject.next(user);
        })
      );
  }

signUp(signUp: FormData): Observable<AuthResponse> {
  return this.http.post<AuthResponse>(`${this.apiUrl}/signUp`, signUp, {
    withCredentials: true
  }).pipe(
    tap(user => {
      this.setAuthToken(user.token ?? null);
      this.currentUserSubject.next(user);
    })
  );
}

  private loadCurrentUser(): Observable<AuthResponse | null> {
    return this.http.get<AuthResponse>(`${this.apiUrl}/me`, {
      withCredentials: true,
      headers: this.getAuthHeaders()
    })
      .pipe(
        tap(user => {
          if (user?.token) {
            this.setAuthToken(user.token);
          }
          this.currentUserSubject.next(user);
        }),
        catchError(() => {
          this.setAuthToken(null);
          this.currentUserSubject.next(null);
          return of(null);
        })
      );
  }

  /**
   * שולח מידע מעודכן של המשתמש לשרת (כגון תמונת פרופיל) ומעדכן
   * את BehaviorSubject עם התגובה.
   */
  updateProfile(formData: FormData): Observable<AuthResponse> {
    return this.http.put<AuthResponse>(`${this.apiUrl}/update`, formData, { withCredentials: true })
      .pipe(
        tap(user => {
          this.currentUserSubject.next(user);

          const currentUser = this.currentUserSubject.value;
          if (currentUser) {
            currentUser.image = user.image ?? currentUser.image;
            currentUser.imagePath = user.imagePath ?? currentUser.imagePath;
            currentUser.imageUrl = user.imageUrl ?? currentUser.imageUrl;
            this.currentUserSubject.next(currentUser);
          }
        })
      );
  }
signOut(): Observable<any> {
    // 1. נעדכן את ה-BehaviorSubject ל-null באופן מיידי.
    // זה יגרום לכל הקומפוננטות המנויות (כמו ה-Header) לעדכן את עצמן מיד.
    this.setAuthToken(null);
    this.currentUserSubject.next(null); 
    
    // 2. נשלח את הבקשה לשרת כדי למחוק את הקוקי.
    return this.http.post(`${this.apiUrl}/signOut`, {}, {
      withCredentials: true,
      headers: this.getAuthHeaders()
    })
        .pipe(
            catchError(err => {
                console.error('Sign out failed on server, but local state cleared.', err);
                return of(null); 
            })
        );
}

  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

getCurrentUser(): Observable<AuthResponse | null> {
  return this.currentUserSubject.asObservable();
}
}
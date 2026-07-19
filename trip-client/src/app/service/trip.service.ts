import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import TripDTO, { TripToUpload, TripListDTO } from '../models/trip.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TripService {

  private readonly baseUrl = 'http://localhost:8080/api/trip';

  constructor(private _httpClient: HttpClient) { }


  getTrips(): Observable<TripListDTO[]> {
    return this._httpClient.get<TripListDTO[]>(`${this.baseUrl  }/trips`, { withCredentials: true });
  }

  getTripsByCategoryId(id: number[]): Observable<TripListDTO[]> {
    return this._httpClient.get<TripListDTO[]>(`${this.baseUrl}/tripsByCategoryId/${id}`, { withCredentials: true });
  }

  getTripById(id: number): Observable<TripDTO> {
    return this._httpClient.get<TripDTO>(`${this.baseUrl}/getTripById/${id}`, { withCredentials: true });
  }
  
  getTripsByUserId(id: number): Observable<TripListDTO[]> {
    return this._httpClient.get<TripListDTO[]>(`${this.baseUrl}/tripsByUserId/${id}`, { withCredentials: true });
  }

  uploadTrip(tripToUpload: FormData): Observable<TripToUpload> {
    const token = typeof window !== 'undefined' ? window.localStorage.getItem('authToken') : null;
    const headers = new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : ''
    });

    return this._httpClient.post<TripToUpload>(`${this.baseUrl}/uploadTrip`, tripToUpload, {
      withCredentials: true,
      headers
    });
  }

  deleteTripByAdmin(id: number): Observable<any> {
    const token = typeof window !== 'undefined' ? window.localStorage.getItem('authToken') : null;
    const headers = new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : ''
    });
    return this._httpClient.delete(`${this.baseUrl}/deleteTripByAdmin/${id}`, {
      withCredentials: true,
      headers
    });
  }

  getDashboardStats(): Observable<{ totalTrips: number; totalUsers: number }> {
    const token = typeof window !== 'undefined' ? window.localStorage.getItem('authToken') : null;
    const headers = new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : ''
    });
    return this._httpClient.get<{ totalTrips: number; totalUsers: number }>(`${this.baseUrl}/admin/dashboard-stats`, {
      withCredentials: true,
      headers
    });
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import Trip, { TripToUpload } from '../models/trip.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TripService {

  private readonly apiUrl = 'http://localhost:8080/api/trips';

  constructor(private _httpClient: HttpClient) { }

  getTrips(): Observable<Trip[]> {
    return this._httpClient.get<Trip[]>(`${this.apiUrl}/tasks`);
  }

  getTripById(id: number): Observable<Trip> {
    return this._httpClient.get<Trip>(`${this.apiUrl}/getTripsById/${id}`);
  }

  getTripsByUserId(id: number): Observable<Trip[]> {
    return this._httpClient.get<Trip[]>(`${this.apiUrl}/tripsByUserId/${id}`);
  }

  getTripsByCategoryId(id: number, id2: number): Observable<Trip[]> {
    return this._httpClient.get<Trip[]>(`${this.apiUrl}/tripsByCatgoriesId/${id}/${id2}`);
  }

  uploadTrip(data: TripToUpload | FormData): Observable<any> {
    return this._httpClient.post(`${this.apiUrl}/uploadTrip`, data);
  }
}
import { HttpClient } from '@angular/common/http';
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

  getTripsByCategories(categoryIds: number[]): Observable<TripListDTO[]> {
    const ids = categoryIds.join(',');
    return this._httpClient.get<TripListDTO[]>(`${this.baseUrl}/tripsByCategories?ids=${ids}`, { withCredentials: true });
  }


  getTripById(id: number): Observable<TripDTO> {
    return this._httpClient.get<TripDTO>(`${this.baseUrl}/getTripById/${id}`, { withCredentials: true });
  }
  getTripsByUserId(id: number): Observable<TripListDTO[]> {
    return this._httpClient.get<TripListDTO[]>(`${this.baseUrl}/tripsByUserId/${id}`, { withCredentials: true });
  }

  uploadTrip(tripToUpload: FormData): Observable<TripToUpload> {
    return this._httpClient.post<TripToUpload>(`${this.baseUrl}/uploadTrip`, tripToUpload, { withCredentials: true });
  }
  updateTrip(id: number, tripToUpdate: TripToUpload): Observable<TripToUpload> {
    return this._httpClient.put<TripToUpload>(`${this.baseUrl}/updateTrip/${id}`, tripToUpdate, { withCredentials: true });
}

   deleteTrip(id: number): Observable<void> {
    return this._httpClient.delete<void>(`${this.baseUrl}/deleteTrip/${id}`, { withCredentials: true });
  }
}

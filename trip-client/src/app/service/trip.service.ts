import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import Trip,{ TripToAdd } from '../models/trip.model';

import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TripService {
  
  constructor(private _httpClient: HttpClient) { }

  getTrips(): Observable<Trip[]> {
    return this._httpClient.get<Trip[]>('http://localhost:8080/api/trips/tasks');
  }

  getTripById(id: number): Observable<Trip> {
    return this._httpClient.get<Trip>(`http://localhost:8080/api/trips/getTripsById/${id}`)
  }

  getTripsByUserId(id: number): Observable<Trip[]> {
    return this._httpClient.get<Trip[]>(` http://localhost:8080/api/trips/tripsByUserId/${id}`)
  }

  getTripsByCategoryId(id: number, id2: number): Observable<Trip[]> {
     return this._httpClient.get<Trip[]>(` http://localhost:8080/api/trips/tripsByCatgoriesId/${id}/${id2}`)
  }

  addTrip(trip: TripToAdd): Observable<TripToAdd> {
    return this._httpClient.post<TripToAdd>('http://localhost:8080/api/trips/addTrip', trip)
  }
}

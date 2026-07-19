import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CommentDTO, CommentToAdd } from '../models/comment.model';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private readonly baseUrl = 'http://localhost:8080/api/comment';

  constructor(private _httpClient: HttpClient) { }

  private getAuthHeaders(): HttpHeaders {
    const token = typeof window !== 'undefined' ? window.localStorage.getItem('authToken') : null;
    return new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : ''
    });
  }

  getCommentsByTripId(tripId: number): Observable<CommentDTO[]> {
    return this._httpClient.get<CommentDTO[]>(`${this.baseUrl}/getCommentsByTripsId/${tripId}`, { withCredentials: true });
  }

  addComment(comment: CommentToAdd): Observable<CommentDTO> {
    return this._httpClient.post<CommentDTO>(`${this.baseUrl}/addComment`, comment, {
      withCredentials: true,
      headers: this.getAuthHeaders()
    });
  }

}
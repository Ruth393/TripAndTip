import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import CommentToAdd from '../models/comment.model'


@Injectable({
  providedIn: 'root'
})
export class Comment {
  
  constructor(private _httpClient: HttpClient) { }

    addTask(comment: CommentToAdd): Observable<CommentToAdd> {
      return this._httpClient.post<CommentToAdd>('http://localhost:8080/api/comment/addComment',comment );
    }
}

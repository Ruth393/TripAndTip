import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import Category from '../models/category.model'


@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  constructor(private _httpClient:HttpClient) { }
 
getCategoriesFromServer(): Observable<Category[]>{
  return this._httpClient.get<Category[]>('http://localhost:8080/api/category/categories', { withCredentials: true });
}

getCategoryById(id: number): Observable<Category>{
  return this._httpClient.get<Category>(`http://localhost:8080/api/category/getCategoryById/${id}`, { withCredentials: true });
}
 
 addCategory(category: Partial<Category>): Observable<Category> {
   return this._httpClient.post<Category>('http://localhost:8080/api/category/addCategory', category, { withCredentials: true });
 }
}

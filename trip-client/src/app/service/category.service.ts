import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import Categories from '../models/category.model'


@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  constructor(private _httpClient:HttpClient) { }
 
getCategorysFromServer(): Observable<Categories[]>{
  return this._httpClient.get<Categories[]>('http://localhost:8080/api/category/categorys');
}

getCategoryById(id: number): Observable<Categories>{
  return this._httpClient.get<Categories>(`http://localhost:8080/api/category/getCategoryById/${id}`);
}
 
 addCategory(category: Partial<Categories>): Observable<Categories> {
   return this._httpClient.post<Categories>('http://localhost:8080/api/category/addCategory', category);
 }
}

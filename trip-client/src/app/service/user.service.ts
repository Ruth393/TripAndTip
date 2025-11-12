import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import Users, { SignIn,SignUp } from '../models/user.model'


@Injectable({
  providedIn: 'root'
})
export class  UserService {
  constructor(private _httpClient:HttpClient) { }
 
getUsersFromServer(): Observable<Users[]>{
  return this._httpClient.get<Users[]>('http://localhost:8080/api/users/users');
}

signIn(signIn: SignIn):Observable<Users>{
  return this._httpClient.post<Users>('http://localhost:8080/api/users/signIn', signIn);
}

signUp(signUp: SignUp):Observable<Users>{
  return this._httpClient.post<Users>('http://localhost:8080/api/users/signUp', signUp);
}

}

import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {LoginResponse} from '../models/login-response';
import {LoginRequest} from '../models/login-request';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private readonly http = inject(HttpClient);
  private readonly authApiUrl = environment.apiUrl + '/auth';

  public login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.authApiUrl, request)
  }

}

import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {LoginResponse} from '../models/login-response';
import {LoginRequest} from '../models/login-request';
import {environment} from '../../environments/environment';
import {Observable, tap} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private readonly http = inject(HttpClient);
  private readonly authApiUrl = environment.apiUrl + '/auth';
  private accessToken: string | null = null;

  public login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.authApiUrl, request, {withCredentials: true}).pipe(
      tap(response => {
        this.accessToken = response.token;
      })
    );
  }

  public refreshToken(): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.authApiUrl}/refresh`, {}, {withCredentials: true}).pipe(
      tap(response => {
        this.accessToken = response.token;
      })
    );
  }

  public logout(): Observable<void> {
    this.accessToken = null;
    return this.http.post<void>(`${this.authApiUrl}/logout`, {}, {withCredentials: true});
  }

  public getAccessToken(): string | null {
    return this.accessToken;
  }

}

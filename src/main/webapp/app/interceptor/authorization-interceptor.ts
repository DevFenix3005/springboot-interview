import {HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpRequest} from '@angular/common/http';
import {inject} from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {catchError, switchMap} from 'rxjs/operators';
import {AuthService} from '../services/auth.service';

export function authorizationInterceptor(
  request: HttpRequest<unknown>, next: HttpHandlerFn
): Observable<HttpEvent<unknown>> {
  const authService = inject(AuthService);
  const accessToken = authService.getAccessToken();

  const isAuthEndpoint = request.url.includes('/auth');
  const requestWithToken = accessToken && !isAuthEndpoint ? request.clone({
    setHeaders: {
      Authorization: `Bearer ${accessToken}`
    }
  }) : request;

  return next(requestWithToken).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status !== 401 || isAuthEndpoint) {
        return throwError(() => error);
      }
      return authService.refreshToken().pipe(
        switchMap(() => {
          const refreshedToken = authService.getAccessToken();
          if (!refreshedToken) {
            return throwError(() => error);
          }
          const retryRequest = request.clone({
            setHeaders: {
              Authorization: `Bearer ${refreshedToken}`,
            },
          });
          return next(retryRequest);
        })
      );
    })
  )

}

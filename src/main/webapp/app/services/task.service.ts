import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {TaskRequest} from '../models/task-request';
import {Observable} from 'rxjs';
import {TaskResponse} from '../models/task-response';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private readonly taskUrl = environment.apiUrl + '/tasks';
  private readonly http = inject(HttpClient);
  private readonly headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});

  public addTasks(taskRequest: TaskRequest): Observable<TaskResponse> {
    return this.http.post<TaskResponse>(this.taskUrl, taskRequest, {headers: this.headers})
  }

  public getAllTasks(): Observable<TaskResponse[]> {
    return this.http.get<TaskResponse[]>(this.taskUrl, {headers: this.headers})
  }


}

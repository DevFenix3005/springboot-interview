import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
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

  public addTasks(taskRequest: TaskRequest): Observable<TaskResponse> {
    return this.http.post<TaskResponse>(this.taskUrl, taskRequest, {
      headers: {'Content-Type': 'application/json', 'Authorization': `Bearer ${localStorage.getItem('token')}`},
    })
  }

  public getAllTasks(): Observable<TaskResponse[]> {
    return this.http.get<TaskResponse[]>(this.taskUrl, {
      headers: {'Content-Type': 'application/json', 'Authorization': `Bearer ${localStorage.getItem('token')}`},
    })
  }


}

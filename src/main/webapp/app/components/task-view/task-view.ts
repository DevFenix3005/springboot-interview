import {Component, inject, OnInit} from '@angular/core';
import {SubmitTaskResult, TaskForm} from '../task-form/task-form';
import {TaskList} from '../task-list/task-list';
import {TaskService} from '../../services/task.service';
import {TaskRequest} from '../../models/task-request';
import {MatSnackBar} from '@angular/material/snack-bar';
import {TaskResponse} from '../../models/task-response';

@Component({
  selector: 'app-task-view',
  imports: [
    TaskForm,
    TaskList
  ],
  templateUrl: './task-view.html',
  styleUrl: './task-view.scss',
})
export class TaskView implements OnInit {

  private readonly taskService = inject(TaskService);
  private readonly snackBar = inject(MatSnackBar);
  tasks: TaskResponse[] = [];
  protected submitStatus: SubmitTaskResult = {done: false};

  ngOnInit(): void {
    this.loadTasks();
  }

  private loadTasks() {
    this.taskService.getAllTasks().subscribe(tasks => {
      this.tasks = tasks;
    })

  }

  sendTaskRequestToService($event: TaskRequest) {
    this.taskService.addTasks($event).subscribe({
      next: taskResponse => {
        this.loadTasks();
        this.submitStatus = {done: true};
        this.snackBar.open("Task added successfully", 'Close', {duration: 3000});
      },
      error: () => {
        this.submitStatus = {done: true};
        this.snackBar.open("The task can't be added", 'Close', {duration: 3000});
      }
    })
  }


}

import {Component, effect, input, computed, InputSignal} from '@angular/core';
import {MatListModule} from '@angular/material/list';
import {TaskResponse} from '../../models/task-response';
import {MatIcon} from '@angular/material/icon';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-task-list',
  imports: [MatListModule, MatIcon, DatePipe],
  templateUrl: './task-list.html',
  styleUrl: './task-list.scss',
})
export class TaskList {
  tasks: InputSignal<TaskResponse[]> = input.required<TaskResponse[]>();
  taskListEmpty = computed(() => this.tasks().length === 0);

  constructor() {
    effect(() => {
      console.log('Tasks updated:', this.tasks());
    })

  }
}

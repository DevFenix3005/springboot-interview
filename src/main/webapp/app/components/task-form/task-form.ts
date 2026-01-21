import {Component, effect, EventEmitter, input, InputSignal, Output} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';

import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatOption, MatSelect} from '@angular/material/select';
import {TaskRequest} from '../../models/task-request';

export interface SubmitTaskResult {
  done: boolean;
}


@Component({
  selector: 'app-task-form',
  imports: [MatFormFieldModule, MatIconModule, MatInputModule, MatButtonModule, FormsModule, ReactiveFormsModule, MatSelect, MatOption],
  templateUrl: './task-form.html',
  styleUrl: './task-form.scss',
})
export class TaskForm {

  @Output() formSubmitted = new EventEmitter<TaskRequest>();
  priorities: string[] = ['Low', 'Medium', 'High'];
  taskSubmitStatus: InputSignal<SubmitTaskResult> = input.required<SubmitTaskResult>();
  taskFormGroup: FormGroup = new FormGroup({
    title: new FormControl('', [Validators.required, Validators.minLength(3)]),
    priority: new FormControl(this.priorities[1].toUpperCase(), [Validators.required]),
  });

  constructor() {
    effect(() => {
      let {done} = this.taskSubmitStatus();
      if (done) {
        this.taskFormGroup.reset({priority: this.priorities[1].toUpperCase()});
      }
    });
  }

  protected onSubmit() {
    if (this.taskFormGroup.valid) {
      let task = this.taskFormGroup.value as TaskRequest;
      this.formSubmitted.emit(task);
    }
  }

}

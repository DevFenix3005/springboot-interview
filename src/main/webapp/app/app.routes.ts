import {Routes} from '@angular/router';
import {Login} from './components/login/login';
import {TaskView} from './components/task-view/task-view';

export const routes: Routes = [
  {
    path: '',
    component: Login,
  },
  {
    path: 'tasks',
    component: TaskView,
  }
];

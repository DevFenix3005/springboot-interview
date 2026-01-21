import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

bootstrapApplication(App, appConfig)
  // eslint-disable-next-line no-console
  .then(()=> console.log("Application bootstrapped successfully"))
  .catch((err) => console.error(err)); // NOSONAR

import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ApplicationConfigService {
  private endpointPrefix = environment.SERVER_API_URL;

  getEndpointFor(api: string, microservice?: string): string {
    if (microservice) {
      return `${this.endpointPrefix}/services/${microservice}/${api}`;
    }

    return `${this.endpointPrefix}/${api}`;
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {ApplicationConfigService} from '../config/application-config.service';
import {
  API_PAYMENT
} from '../../constants/api.constants';
import {Payment} from '../models/bookings.model';
import {Observable} from 'rxjs';
import {BaseResponse} from '../models/response.model';

@Injectable({
  providedIn: 'root',
})
export class BookingService {

  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  private paymentApi = this.applicationConfigService.getEndpointFor(API_PAYMENT);

  payment(payment: any): Observable<BaseResponse<any>> {
    return this.http.post<BaseResponse<any>>(this.paymentApi, payment);
  }
}

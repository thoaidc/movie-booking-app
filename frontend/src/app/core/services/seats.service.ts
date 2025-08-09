import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {ApplicationConfigService} from '../config/application-config.service';
import {API_SEARCH_SEATS, API_SEARCH_SEATS_SHOW} from '../../constants/api.constants';
import {Observable} from 'rxjs';
import {BaseResponse} from '../models/response.model';
import {Seat, SeatShow} from '../models/seats.model';

@Injectable({
  providedIn: 'root',
})
export class SeatService {

  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  private searchSeatsApi = this.applicationConfigService.getEndpointFor(API_SEARCH_SEATS);
  private searchSeatsShowApi = this.applicationConfigService.getEndpointFor(API_SEARCH_SEATS_SHOW);

  getSeatsByShowId(showId: number): Observable<BaseResponse<Seat[]>> {
    return this.http.get<BaseResponse<Seat[]>>(`${this.searchSeatsApi}/${showId}`);
  }

  getSeatsShowByShowId(showId: number): Observable<BaseResponse<SeatShow[]>> {
    return this.http.get<BaseResponse<SeatShow[]>>(`${this.searchSeatsShowApi}/${showId}`);
  }
}

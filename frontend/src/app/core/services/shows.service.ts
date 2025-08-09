import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {ApplicationConfigService} from '../config/application-config.service';
import {API_SEARCH_SHOWS} from '../../constants/api.constants';
import {BaseFilterRequest} from '../models/request.model';
import {Observable} from 'rxjs';
import {BaseResponse} from '../models/response.model';
import {Shows} from '../models/shows.model';
import {createSearchRequestParams} from '../utils/request.util';

@Injectable({
  providedIn: 'root',
})
export class ShowsService {

  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  private showsFilterApi = this.applicationConfigService.getEndpointFor(API_SEARCH_SHOWS);

  getShowsByMovieId(movieId: number, searchRequest: BaseFilterRequest): Observable<BaseResponse<Shows[]>> {
    const params = createSearchRequestParams(searchRequest);
    return this.http.get<BaseResponse<Shows[]>>(`${this.showsFilterApi}/${movieId}`, {params: params});
  }
}

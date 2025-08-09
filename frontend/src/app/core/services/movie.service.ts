import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {ApplicationConfigService} from '../config/application-config.service';
import {API_SEARCH_MOVIES} from '../../constants/api.constants';
import {BaseFilterRequest} from '../models/request.model';
import {Observable} from 'rxjs';
import {BaseResponse} from '../models/response.model';
import {createSearchRequestParams} from '../utils/request.util';
import {Movie} from '../models/movies.model';

@Injectable({
  providedIn: 'root',
})
export class MovieService {

  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  private movieFilterApi = this.applicationConfigService.getEndpointFor(API_SEARCH_MOVIES);

  getMoviesWithPaging(searchRequest: BaseFilterRequest): Observable<BaseResponse<Movie[]>> {
    const params = createSearchRequestParams(searchRequest);
    return this.http.get<BaseResponse<Movie[]>>(this.movieFilterApi, {params: params});
  }
}

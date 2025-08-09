import {Component, OnDestroy, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {LoadingBarModule} from '@ngx-loading-bar/core';
import {Subscription} from 'rxjs';
import {WebsocketService} from './core/services/websocket.service';
import {DateFilterComponent} from './shared/components/date-filter/date-filter.component';
import {NgbDatepickerModule, NgbModal, NgbModalRef, NgbPagination} from '@ng-bootstrap/ng-bootstrap';
import {NgSelectModule} from '@ng-select/ng-select';
import {SafeHtmlPipe} from './shared/pipes/safe-html.pipe';
import {DecimalPipe, NgFor} from '@angular/common';
import {FormsModule} from '@angular/forms';
import dayjs from 'dayjs/esm';
import {ICON_RELOAD, ICON_SEARCH} from './shared/utils/icon';
import {PAGINATION_PAGE_SIZE} from './constants/common.constants';
import {BookingModalComponent} from './layouts/booking-modal/booking-modal.component';
import {BaseFilterRequest} from './core/models/request.model';
import {Movie} from './core/models/movies.model';
import {MovieService} from './core/services/movie.service';
import {UtilsService} from './shared/utils/utils.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    LoadingBarModule,
    DateFilterComponent,
    NgbDatepickerModule,
    NgSelectModule,
    NgbPagination,
    SafeHtmlPipe,
    NgFor,
    FormsModule,
    DecimalPipe
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit, OnDestroy {
  private stateSubscription: Subscription | null = null;
  private modalRef: NgbModalRef | undefined;
  periods: number = 1;  // Today
  totalItems: number = 0;
  moviesFilter = {
    page: 1,
    size: 10,
    keyword: '',
    fromDate: dayjs(),
    toDate: dayjs().endOf('day')
  }

  isLoading = false;
  movies: Movie[] = [];

  constructor(
    private websocketService: WebsocketService,
    private modalService: NgbModal,
    private movieService: MovieService,
    private utilsService: UtilsService
  ) {}

  ngOnInit(): void {
    this.onSearch();
    this.stateSubscription = this.websocketService.onState().subscribe();
    this.websocketService.connect();
  }

  onTimeChange(even: any) {
    this.moviesFilter.fromDate = even.fromDate;
    this.moviesFilter.toDate = even.toDate;
    this.moviesFilter.page = 1;
    this.periods = even.periods;
    this.onSearch();
  }

  onSearch() {
    const searchRequest: BaseFilterRequest = {
      page: this.moviesFilter.page - 1,
      size: this.moviesFilter.size
    }

    if (this.moviesFilter.keyword && this.moviesFilter.keyword !== '') {
      searchRequest.keyword = this.moviesFilter.keyword;
    }

    if (this.moviesFilter.fromDate) {
      const fromDate = this.moviesFilter.fromDate.toString();
      searchRequest.fromDate = this.utilsService.convertToDateString(fromDate, 'YYYY-MM-DD HH:mm:ss');
    }

    if (this.moviesFilter.toDate) {
      const toDate = this.moviesFilter.toDate.toString();
      searchRequest.toDate = this.utilsService.convertToDateString(toDate, 'YYYY-MM-DD HH:mm:ss');
    }

    this.movieService.getMoviesWithPaging(searchRequest).subscribe(response => {
      this.movies = response.result || [];
      this.totalItems = response.total || 0;
    });
  }

  onReload() {
    this.periods = 1;
    this.moviesFilter = {
      page: 1,
      size: 10,
      keyword: '',
      fromDate: dayjs(),
      toDate: dayjs().endOf('day')
    }

    this.onSearch();
  }

  loadMore($event: any) {
    this.moviesFilter.page = $event;
    this.onSearch();
  }

  openBookingModal(movie: Movie) {
    this.modalRef = this.modalService.open(BookingModalComponent, {backdrop: 'static', size: 'lg'});
    this.modalRef.componentInstance.movie = movie;
    this.modalRef.closed.subscribe(() => this.onReload());
  }

  ngOnDestroy(): void {
    if (this.stateSubscription) {
      this.stateSubscription.unsubscribe();
    }

    this.websocketService.disconnect();
  }

  protected readonly ICON_RELOAD = ICON_RELOAD;
  protected readonly ICON_SEARCH = ICON_SEARCH;
  protected readonly PAGINATION_PAGE_SIZE = PAGINATION_PAGE_SIZE;
}

import {ChangeDetectorRef, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {NgbActiveModal, NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {Location, NgClass, NgFor, NgIf} from '@angular/common';
import {Seat, SeatShow, SeatStatus} from '../../core/models/seats.model';
import {Movie} from '../../core/models/movies.model';
import {NgSelectComponent} from '@ng-select/ng-select';
import {Shows} from '../../core/models/shows.model';
import {FormsModule} from '@angular/forms';
import {ShowsService} from '../../core/services/shows.service';
import {BaseFilterRequest} from '../../core/models/request.model';
import {SeatService} from '../../core/services/seats.service';
import {Booking} from '../../core/models/bookings.model';
import {BookingService} from '../../core/services/bookings.service';
import {WebsocketService} from '../../core/services/websocket.service';
import {ToastrService} from 'ngx-toastr';
import {IMessage} from '@stomp/stompjs';
import {Subscription} from 'rxjs';
import {BaseResponse} from '../../core/models/response.model';
import {PaymentModalComponent} from '../payment-modal/payment-modal.component';

@Component({
  selector: 'app-booking-modal',
  standalone: true,
  imports: [
    NgFor,
    NgIf,
    NgClass,
    NgSelectComponent,
    FormsModule
  ],
  templateUrl: './booking-modal.component.html',
  styleUrl: './booking-modal.component.scss'
})
export class BookingModalComponent implements OnInit, OnDestroy {
  @Input() movie!: Movie;
  private modalRef: NgbModalRef | undefined;
  movieShows: Shows[] = [];
  selectedShowId: number = 0;
  selectedShowInfo: Shows | undefined;
  selectedSeats: number[] = [];
  seats: Seat[] = [];
  seatsShow: SeatShow[] = [];
  step: string = 'INIT';
  bookingStatus: string[] = [];
  private topicSubscription: Subscription | null = null;
  private bookingId: number = 0;
  private totalPayment: number = 0;

  constructor(
    public activeModal: NgbActiveModal,
    private modalService: NgbModal,
    private location: Location,
    private showService: ShowsService,
    private seatService: SeatService,
    private cdr: ChangeDetectorRef,
    private bookingService: BookingService,
    private webSocketService: WebsocketService,
    private toast: ToastrService
  ) {
    this.location.subscribe(() => {
      this.activeModal.dismiss(false);
    });
  }

  ngOnInit(): void {
    if (this.movie && this.movie.id) {
      this.searchMovieShows();
    }
  }

  toggleSeatSelection(seat: SeatShow) {
    if (seat.status !== SeatStatus.AVAILABLE || this.step === 'COMPLETED')
      return;

    const index = this.selectedSeats.indexOf(seat.id);

    if (index > -1) {
      this.selectedSeats.splice(index, 1);
    } else {
      this.selectedSeats.push(seat.id);
    }
  }

  isSelected(seatId: number): boolean {
    return this.selectedSeats.includes(seatId);
  }

  searchMovieShows() {
    const searchRequest: BaseFilterRequest = {
      page: 0,
      size: 100
    }

    this.showService.getShowsByMovieId(this.movie.id, searchRequest).subscribe(response => {
      this.movieShows = response.result || [];
      this.selectedShowId = this.movieShows[0].id;
      this.changeShows();
    })
  }

  changeShows() {
    this.selectedShowInfo = this.movieShows.find(s => s.id === this.selectedShowId);
    this.selectedSeats = [];

    this.seatService.getSeatsShowByShowId(this.selectedShowId).subscribe(response => {
      this.seatsShow = response.result || [];

      this.seatService.getSeatsByShowId(this.selectedShowId).subscribe(response => {
        this.seats = response.result || [];

        this.seatsShow = this.seatsShow.map(seatShow => {
          const seat = this.seats.find(s => s.id === seatShow.seatId);

          return {
            ...seatShow,
            seatNumber: seat?.seatNumber,
            seatRow: seat?.seatRow,
            code: seat?.code
          };
        });

        this.cdr.detectChanges();
      });
    });
  }

  get rows(): number[] {
    return [...new Set(this.seatsShow.map(s => s.seatRow || 0))].sort();
  }

  get seatsByRow(): { [key: number]: SeatShow[] } {
    const map: { [key: number]: SeatShow[] } = {};

    for (let row of this.rows) {
      map[row] = this.seatsShow.filter(s => s.seatRow === row)
        .sort((a, b) => ((a.seatNumber || 0) - (b.seatNumber || 0)));
    }

    return map;
  }

  createBooking() {
    const bookingRequest: Booking = {
      movieId: this.selectedShowInfo?.movieId || 0,
      showId: this.selectedShowId,
      totalAmount: (this.selectedShowInfo?.ticketPrice || 0) * this.selectedSeats.length,
      seatIds: this.selectedSeats
    }

    this.bookingService.payment(null).subscribe(response => {
      if (response.status && response.result > 0) {
        this.bookingId = response.result;
        this.totalPayment = bookingRequest.totalAmount;
        this.step = 'VERIFY';

        this.topicSubscription = this.webSocketService.subscribeToTopic('/topics/bookings').subscribe({
          next: (message: IMessage) => this.handleWebSocketMessage(message),
          error: (e) => console.log(e)
        })
      } else {
        this.toast.error('Tạo yêu cầu đặt vé thất bại', 'Thông báo');
      }
    });
  }

  openPaymentModal(bookingId: number, totalPayment: number) {
    this.modalRef = this.modalService.open(PaymentModalComponent, {backdrop: 'static'});
    this.modalRef.componentInstance.bookingId = bookingId;
    this.modalRef.componentInstance.totalPayment = totalPayment;
    this.modalRef.componentInstance.initialize();
  }

  private handleWebSocketMessage(message: IMessage) {
    const response: BaseResponse<any> = JSON.parse(message.body) as BaseResponse<any>;
    this.bookingStatus.push(response.message || 'Processing');

    if (response.status) {
      if (response.result) {
        this.step = response.result;

        if (response.result === 'VERIFY_CUSTOMER') {
          // this.openVerifyCustomerModal(this.bookingId);
        }

        if (response.result === 'PAYMENT') {
          this.openPaymentModal(this.bookingId, this.totalPayment);
        }
      }
    } else {
      if (response.result) {
        if (response.result === 'VERIFY_CUSTOMER') {
          this.step = 'VERIFY_CUSTOMER_FAILED';
        }

        if (response.result === 'PAYMENT') {
          this.step = 'PAYMENT_FAILED';
        }
      }
    }
  }

  dismiss() {
    this.activeModal.dismiss(false);
  }

  ngOnDestroy() {
    if (this.topicSubscription) {
      this.topicSubscription.unsubscribe();
      this.webSocketService.unsubscribeFromTopic('/topics/bookings');
    }
  }
}

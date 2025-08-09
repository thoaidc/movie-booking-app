import {Component, Input} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {Location} from '@angular/common';
import {BookingService} from '../../core/services/bookings.service';
import {ToastrService} from 'ngx-toastr';
import {Payment} from '../../core/models/bookings.model';

@Component({
  selector: 'app-payment-modal',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './payment-modal.component.html',
  styleUrl: './payment-modal.component.scss'
})
export class PaymentModalComponent {
  paymentInfo: Payment = {
    atm: '',
    pin: '',
    amount: 0,
    bookingId: 0
  }

  @Input() bookingId: number = 0;
  @Input() totalPayment: number = 0;

  constructor(
    public activeModal: NgbActiveModal,
    private location: Location,
    private bookingService: BookingService,
    private toast: ToastrService
  ) {
    this.location.subscribe(() => {
      this.activeModal.dismiss(false);
    });
  }

  initialize() {
    this.paymentInfo.bookingId = this.bookingId;
    this.paymentInfo.amount = this.totalPayment;
  }

  confirm() {
    this.initialize();

    this.bookingService.payment(this.paymentInfo).subscribe(response => {
      if (response.status) {
        this.toast.success('Thanh toán đang được xử lý', 'Thông báo');
      } else {
        this.toast.error('Gửi yêu cầu thanh toán thất bại', 'Thông báo');
      }

      this.activeModal.close(response.status);
    });
  }

  dismiss() {
    this.activeModal.dismiss(false);
  }
}

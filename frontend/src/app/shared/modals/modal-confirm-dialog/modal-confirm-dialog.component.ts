import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {Location, NgClass} from '@angular/common';

@Component({
  selector: 'app-modal-confirm-dialog',
  standalone: true,
  imports: [NgClass],
  templateUrl: './modal-confirm-dialog.component.html',
  styleUrls: ['./modal-confirm-dialog.component.scss'],
})
export class ModalConfirmDialogComponent {
  title = '';
  classBtn = '';

  constructor(
    public activeModal: NgbActiveModal,
    private location: Location,
  ) {
    this.location.subscribe(() => {
      this.activeModal.dismiss(false);
    });
  }

  dismiss() {
    this.activeModal.dismiss(false);
  }

  onConfirm() {
    this.activeModal.close(true);
  }
}

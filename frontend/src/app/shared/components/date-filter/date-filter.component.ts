import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import dayjs from 'dayjs/esm';
import {LIST_TIME_SELECT} from '../../../constants/common.constants';
import {NgSelectModule} from '@ng-select/ng-select';
import {FormsModule} from '@angular/forms';
import {NgbDatepickerModule} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-date-filter',
  standalone: true,
  imports: [
    NgSelectModule,
    FormsModule,
    NgbDatepickerModule
  ],
  templateUrl: './date-filter.component.html',
  styleUrls: ['./date-filter.component.scss'],
})
export class DateFilterComponent implements OnChanges {
  @Input() periods: number = 1;
  @Input() clearable: boolean = true;
  @Output() timeChange = new EventEmitter<any>();
  fromDate: dayjs.Dayjs | any;
  toDate: dayjs.Dayjs | any;

  ngOnChanges(changes: SimpleChanges) {
    if (changes['periods']) {
      this.periods = changes['periods'].currentValue;
    }
  }

  changePeriods() {
    const time = this.getTime(this.periods);
    this.timeChange.emit(time);
  }

  getTime(periods: any) {
    switch (periods) {
      case 1: // TODAY
        return {
          fromDate: dayjs(),
          toDate: dayjs().endOf('day'),
          periods: periods,
        };
      case 2: // TOMORROW
        return {
          fromDate: dayjs().add(1, 'day').startOf('day'),
          toDate: dayjs().add(1, 'day').endOf('day'),
          periods,
        };
      case 3: // THIS_WEEK
        return {
          fromDate: dayjs(),
          toDate: dayjs().endOf('week'),
          periods: periods,
        };
      case 4: // NEXT 7 DAYS
        return {
          fromDate: dayjs(),
          toDate: dayjs().add(7, 'day').endOf('day'),
          periods: periods,
        };
      case 5: // THIS_MONTH
        return {
          fromDate: dayjs(),
          toDate: dayjs().endOf('month'),
          periods: periods,
        };
      default:
        return {
          fromDate: dayjs(),
          toDate: dayjs().endOf('day'),
          periods: periods,
        };
    }
  }

  protected readonly LIST_TIME_SELECT = LIST_TIME_SELECT;
}

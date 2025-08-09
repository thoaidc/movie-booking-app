import { Injectable } from '@angular/core';
import dayjs from 'dayjs/esm';

@Injectable({
  providedIn: 'root',
})
export class UtilsService {

  convertToDateString(dateString: string, toFormat: string): string {
    const dateFormats = ['MM-DD-YYYY', 'YYYY-MM-DD', 'DD-MM-YYYY', 'YYYY/MM/DD', 'DD/MM/YYYY'];
    const timeFormat = 'HH:mm:ss';
    let date;

    // Check if dateString matches time format
    if (dayjs(dateString, timeFormat, true).isValid()) {
      date = dayjs(dateString, timeFormat, true);
    } else {
      // Check if dateString matches date formats
      date = dayjs(dateString, dateFormats, true);
    }

    return date.isValid() ? date.format(toFormat) : '';
  }
}

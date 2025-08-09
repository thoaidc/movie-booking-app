import { Directive, HostListener, ElementRef } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({
  selector: '[dateFormat]',
  standalone: true
})
export class DateFormatDirective {
  private el: HTMLInputElement;
  private dateFormat = 'DD/MM/YYYY';
  private defaultDate = Array(this.dateFormat.length + 1).join('_');
  private allowedKeys = ['Backspace', 'ArrowLeft', 'ArrowRight'];

  constructor(
    private elementRef: ElementRef,
    private control: NgControl,
  ) {
    this.el = this.elementRef.nativeElement;
  }

  @HostListener('keydown', ['$event'])
  onKeyDown(e: KeyboardEvent) {
    let cursorPosition = this.el.selectionStart || 0;
    let currentValue = this.el.value;
    let key = this.getNormalizedKey(e.key);

    if (this.allowedKeys.includes(e.key)) {
      return;
    }

    if (/\D/g.test(key)) {
      e.preventDefault();
      return;
    }

    const nextChar = this.dateFormat[cursorPosition];
    let newValue;

    if (nextChar === '/') {
      if (currentValue[cursorPosition] !== '/') {
        currentValue = currentValue.slice(0, cursorPosition) + '/' + currentValue.slice(cursorPosition);
      }

      cursorPosition++;
    }

    if (currentValue.length === this.dateFormat.length) {
      e.preventDefault();
      return;
    }

    if (cursorPosition < 3) {
      let date = currentValue.slice(0, 2) + key;
      date = parseInt(date) > 31 ? '31' : date;
      newValue = date + currentValue.slice(2);
    } else if (cursorPosition >= 3 && cursorPosition < 6) {
      let month = currentValue.slice(3, 5) + key;
      month = parseInt(month) > 12 ? '12' : month;
      newValue = currentValue.slice(0, 3) + month + currentValue.slice(5);
    } else {
      newValue = currentValue.slice(0, cursorPosition) + key + ('_' + currentValue.slice(cursorPosition + 1)).slice(1);
    }

    e.preventDefault();

    if (newValue !== this.defaultDate) {
      if (this.control.control) {
        this.control.control.setValue(newValue);
      }
    }

    this.el.value = newValue;
    this.el.setSelectionRange(cursorPosition + 1, cursorPosition + 1);
  }

  getNormalizedKey(key: string) {
    if (key === ' ') return '';
    if (key.length === 1) return key;

    const match = key.match(/^Digit(\d)$/);
    return match ? match[1] : '';
  }
}

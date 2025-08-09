import {Directive, ElementRef, forwardRef, HostListener, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";

@Directive({
  selector: '[alphanumericOnly]',
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AlphanumericOnlyDirective),
      multi: true
    }
  ]
})
export class AlphanumericOnlyDirective implements ControlValueAccessor, OnInit, OnChanges {
  @Input() specialCharacter: boolean = false;
  @Input() isPassword: boolean = false;  // New Input
  private regex: RegExp = /^[a-zA-Z0-9]*$/;
  private specialKeys: Array<string> = ['Backspace', 'Tab', 'End', 'Home', 'ArrowLeft', 'ArrowRight', 'Delete'];
  private timeout: any;
  private originalValue: string = '';
  private onChange: (value: any) => void = () => {};
  private onTouched: () => void = () => {};

  constructor(private el: ElementRef) {}

  ngOnInit() {
    this.updateRegex();
  }
  ngOnChanges(changes: SimpleChanges): void {
    const input = this.el.nativeElement as HTMLInputElement;

    // Check if isPassword has changed
    if (changes['isPassword'] && !changes['isPassword'].firstChange) {
      if (this.isPassword) {
        input.value = '.'.repeat(this.originalValue.length);
      } else {
        input.value = this.originalValue;
      }
    }
  }

  private updateRegex() {
    if (this.specialCharacter) {
      this.regex = new RegExp(/^[a-zA-Z0-9!"#$%&'()*+,-./:;<=>?@[\\\]^_`{|}~ ]*$/);
    } else {
      this.regex = new RegExp(/^[a-zA-Z0-9 ]*$/);
    }
  }

  @HostListener('keydown', ['$event'])
  onKeyDown(event: KeyboardEvent) {
    if (this.specialKeys.indexOf(event.key) !== -1) {
      return;
    }

    let current: string = event.key;
    if (!String(current).match(this.regex)) {
      event.preventDefault();
    }
  }

  @HostListener('paste', ['$event'])
  blockPaste(event: ClipboardEvent) {
    let clipboardData = event.clipboardData;

    if (clipboardData) {
      let pastedText = clipboardData.getData('text');
      if (!pastedText.match(this.regex)) {
        event.preventDefault();
      }
    }
  }

  @HostListener('input', ['$event'])
  onInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const currentLength = input.value.length;
    const previousLength = this.originalValue.length;
    if (currentLength > previousLength) {
      this.originalValue += input.value.slice(previousLength);
    } else {
      this.originalValue = this.originalValue.slice(0, currentLength);
    }

    if (this.isPassword) {
      // Mask the input value
      input.value = '.'.repeat(currentLength);
    }
    // Update the model value with the unmasked value
    this.onChange(this.originalValue);
  }

  @HostListener('keypress') onKeyPress() {
    if (this.timeout) {
      clearTimeout(this.timeout);
    }

    this.timeout = setTimeout(() => {
      const value: string = this.el.nativeElement.value;
      this.el.nativeElement.value = value.replace(/\s+/g, '');
      if (!this.isPassword) {
        this.onChange(this.el.nativeElement.value);
      }
    }, 800);
  }

  // ControlValueAccessor implementation
  writeValue(value: any): void {
    if (this.isPassword) {
      // Mask the value if isPassword is true
      let value = this.el.nativeElement.value;
      this.el.nativeElement.value = '.'.repeat(value ? value.length : 0);
    } else {
      this.el.nativeElement.value = value || '';
    }
  }

  @HostListener('paste', ['$event'])
  onPaste(event: ClipboardEvent): void {
    if (this.isPassword) {
      // Block paste action if isPassword is true
      event.preventDefault();
      return;
    }

    // If isPassword is false, assign the value paste to ngModel
    const clipboardData = event.clipboardData?.getData('text') || '';
    const input = this.el.nativeElement as HTMLInputElement;
    const start = input.selectionStart || 0;
    const end = input.selectionEnd || 0;

    this.originalValue = input.value.slice(0, start) + clipboardData + input.value.slice(end);

    this.el.nativeElement.value = this.originalValue;
    event.preventDefault();
    this.onChange(this.originalValue);
  }

  @HostListener('touch', ['$event'])
  onTouch(event: TouchEvent): void {
    event.preventDefault();
    this.onTouched();
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.el.nativeElement.disabled = isDisabled;
  }
}

import {inject} from '@angular/core';
import {
  HttpErrorResponse,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest
} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {tap} from 'rxjs';

export const ApiInterceptorFn: HttpInterceptorFn = (request: HttpRequest<any>, next: HttpHandlerFn) => {
  const toast = inject(ToastrService);

  return next(request).pipe(
    tap({
      next: () => {},
      error: (error: HttpErrorResponse) => {
        if (error.error && error.message) {
          toast.error(error.error.message, 'Thông báo');
        }
      }
    })
  );
};

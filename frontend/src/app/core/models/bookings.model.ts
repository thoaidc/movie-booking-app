export interface Booking {
  showId: number;
  movieId: number;
  totalAmount: number;
  seatIds: number[];
}

export interface Customer {
  fullname: string;
  email: string;
  phone: string;
  bookingId: number;
}

export interface Payment {
  bookingId: number;
  amount: number;
  atm: string;
  pin: string;
}

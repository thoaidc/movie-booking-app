export interface Seat {
  id: number;
  cinemaRoomId: number;
  seatNumber: number;
  seatRow: number;
  code: string;
}

export interface SeatShow {
  id: number;
  showId: number;
  seatId: number;
  status: string;
  seatNumber?: number;
  seatRow?: number;
  code?: string;
}

export const SeatStatus = {
  AVAILABLE: 'AVAILABLE',
  BOOKED: 'BOOKED',
  RESERVED: 'RESERVED',
  SELECTED: 'SELECTED'
}

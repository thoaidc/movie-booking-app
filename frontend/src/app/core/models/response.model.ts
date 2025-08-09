
export interface BaseResponse<T> {
  code: number;
  status: boolean;
  message?: string;
  result?: T;
  total?: number;
}

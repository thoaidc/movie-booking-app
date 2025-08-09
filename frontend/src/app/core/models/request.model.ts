export interface Pagination {
  page: number;
  size: number;
  sort?: string[];
}

export interface BaseFilterRequest extends Pagination {
  fromDate?: string;
  toDate?: string;
  status?: string;
  keyword?: string;
}

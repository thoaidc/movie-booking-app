import { HttpParams } from '@angular/common/http';

/**
 * Create an HttpParams object from an input object (commonly used for query parameters)
 *
 * This function will:
 *   - Ignore keys with values of null, undefined, or empty strings ('')

 *   - Preserve valid values such as `0` and `false` (which are normally treated as falsy in JS)
 *   - Support array values by appending multiple parameters with the same key
 *   - Handle the `sort` key separately to preserve sorting order
 *
 * Example:
 *  ```ts
 *  const req = {
 *    page: 1,
 *    size: 20,
 *    sort: ['name,asc', 'createdDate,desc'],
 *    keyword: 'test',
 *    isActive: false,
 *    count: 0,
 *    skipEmpty: ''
 *  };
 *
 *  const params = createSearchRequestParams(req);
 *  ```
 * params.toString() will result in: <p>
 * ?page=1&size=20&keyword=test&isActive=false&count=0&sort=name,asc&sort=createdDate,desc
 *
 * @param req An object containing key-value pairs to be converted into a query string
 * @returns HttpParams to be used with HttpClient
 * @author thoaidc
 */
export const createSearchRequestParams = (req?: any): HttpParams => {
  let options: HttpParams = new HttpParams();

  if (req) {
    Object.keys(req).forEach(key => {
      if (key !== 'sort' && (req[key] || req[key] === 0 || req[key] === false)) {
        for (const value of [].concat(req[key]).filter(v => v !== '')) {
          options = options.append(key, value);
        }
      }
    });

    if (req.sort) {
      req.sort.forEach((val: string) => {
        options = options.append('sort', val);
      });
    }
  }

  return options;
};

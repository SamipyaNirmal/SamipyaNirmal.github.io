import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import{Observable,of} from 'rxjs';
import { AutoData } from './AutoData';

@Injectable({
  providedIn: 'root'
})
export class AutocompleteService {
  private autocompleteUrl='http://localhost:3000/autocomplete';
  mydata:Observable<AutoData[]>;
  constructor( private http: HttpClient) { }
  getData(ticker:String): Observable<AutoData[]>
  {
    if (!ticker.trim())
    {
      return of([]);
    }
    this.mydata=this.http.get<AutoData[]>(`${this.autocompleteUrl}/${ticker}`);
    return this.mydata;
  }
}

import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import{Observable,of} from 'rxjs';
import {BasicDetails} from './BasicDetails';
import {Summary} from "./Summary";

@Injectable({
  providedIn: 'root'
})
export class BasicdetailsService {
  private detailsUrl = 'http://localhost:3000/details';
  private summaryUrl="http://localhost:3000/summary";
  private newsUrl="http://localhost:3000/news";
  private dailychartsUrl="http://localhost:3000/dailycharts";
  private historicalCharts="http://localhost:3000/charts";
  private watchlistUrl="http://localhost:3000/watchlist";
  private portfolioUrl="http://localhost:3000/portfolio";
  mydata: Observable<BasicDetails>;
  constructor( private http: HttpClient) { }
  getDetailsData(ticker: string): Observable<BasicDetails>
  {
    return this.http.get<BasicDetails>(`${this.detailsUrl}/${ticker}`);
  }
  getSummaryData(ticker: string): Observable<Summary>
  {
    return this.http.get<Summary>(`${this.summaryUrl}/${ticker}`);
  }
  getNewsData(ticker: string): Observable<any>
  {
    return this.http.get<any>(`${this.newsUrl}/${ticker}`);
  }
  getDailyData(ticker: string): Observable<any>
  {
    return this.http.get<any>(`${this.dailychartsUrl}/${ticker}`);
  }
  getHistoricalData(ticker: string): Observable<any>
  {
    return this.http.get<any>(`${this.historicalCharts}/${ticker}`);
  }
  getWatchlistUpdatedData(tickers: string): Observable<any>
  {
    return this.http.get<any>(`${this.watchlistUrl}/${tickers}`);
  }
  getPortfolioUpdatedData(tickers: string): Observable<any>
  {
    return this.http.get<any>(`${this.portfolioUrl}/${tickers}`);
  }
}

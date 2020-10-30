import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {BasicDetails} from "./BasicDetails";

@Injectable({
  providedIn: 'root'
})
export class WatchlistServiceService {
  resp:any;
  ans:string;
  temp:any;
  companyName:any;
  constructor() { }
  addWatchlist(details){
    let { ticker, companyName, lastPrice, change, changePercent } = details;
    this.resp={};
    if(localStorage['watchlist'])
    {
      this.resp=JSON.parse(localStorage['watchlist']);
    }
    this.resp[ticker] = JSON.stringify({
      ticker,
      companyName,
      lastPrice,
      change,
      changePercent
    });
    localStorage['watchlist']=JSON.stringify(this.resp);
    //console.log(localStorage);
  }
  removeWatchlist(ticker: string){
    if(localStorage['watchlist'])
    {
      this.resp=JSON.parse(localStorage['watchlist']);
      delete this.resp[ticker];
      //console.log(this.resp);
      localStorage['watchlist']=JSON.stringify(this.resp);
    }
    //console.log(localStorage);
  }
  getWatchlistData()
  {
    let res=[];
    let sortedKeys = [];
    if(localStorage['watchlist'])
    {
      this.resp=JSON.parse(localStorage['watchlist']);
      for (let key in this.resp)
      {
        sortedKeys.push(key);
      }
      sortedKeys.sort();
      sortedKeys.forEach(k =>
      {
        res.push(JSON.parse(this.resp[k]));
      });
    }
    //console.log(res);
    return res;
  }
  contains(ticker):boolean
  {
    if(localStorage['watchlist'])
    {
      this.resp=JSON.parse(localStorage['watchlist']);
      for (let key in this.resp) {
        //console.log(key);
        if(key===ticker)
          return true;
      }
    }
    return false;
  }
  getWatchlistTickers():string
  {
    this.ans="";
    if(localStorage['watchlist'])
    {
      this.resp=JSON.parse(localStorage['watchlist']);
      for (let key in this.resp) {
        this.ans+=key+",";
      }
    }
    return this.ans;
  }
  updateWatchlistData(updatedData)
  {
    this.resp=JSON.parse(localStorage['watchlist']);
    //console.log(updatedData);
    updatedData.forEach(key=>{
      key.forEach(k=>{
        //console.log(k);
        this.temp=JSON.parse(this.resp[k[0]]);
        //console.log(this.temp['companyName']);
        this.companyName=this.temp['companyName'];
        this.resp[k[0]]=JSON.stringify({
          ticker:k[0],
          companyName:this.companyName,
          lastPrice:k[1],
          change:k[2],
          changePercent:k[3]
        });
        localStorage['watchlist']=JSON.stringify(this.resp);
      })
    })
    //console.log(localStorage);
    return this.getWatchlistData();
  }
}

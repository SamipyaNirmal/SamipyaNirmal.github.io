import { Injectable } from '@angular/core';
// @ts-ignore
import {PortfolioData} from './PortfolioData';
@Injectable({
  providedIn: 'root'
})
export class PortfolioService {
  resp:any;
  ans:string;
  totalCost:number;
  quantity:any;
  last:any;
  companyName:any;
  constructor() { }
  addPortfolio(portfoliodetails:PortfolioData){
    let { ticker,companyName,quantity,totalCost,lastPrice } = portfoliodetails;
    localStorage[ticker] = JSON.stringify({
      ticker,
      companyName,
      quantity,
      totalCost,
      lastPrice
    });
   console.log(localStorage);
  }
  getPortfolioData()
  {
    let res=[];
    let sortedKeys = [];
    for (let key in localStorage)
    {
      if(key!=='watchlist' && localStorage.hasOwnProperty(key))
        sortedKeys.push(key);
    }
    sortedKeys.sort();
    sortedKeys.forEach(k =>
    {
      res.push(JSON.parse(localStorage[k]));
    });
    //console.log(res);
    return res;
  }
  getPortfolioTickers():string
  {
    this.ans="";
    for (let key in localStorage)
    {
      if(key==='watchlist')
        continue;
      this.ans+=key+",";
    }
    return this.ans;
  }
  updatePortfolioData(updatedData)
  {
    //console.log(updatedData);
    updatedData.forEach(key=>{
      key.forEach(k=>{
        if(localStorage[k[0]]) {
          this.resp = JSON.parse(localStorage[k[0]]);
          this.quantity=this.resp['quantity'];
          //console.log(this.resp);
          this.totalCost=+this.resp['totalCost'];
          this.companyName=this.resp['companyName'];
          localStorage[k[0]]=JSON.stringify({
            ticker:k[0],
            companyName:this.companyName,
            quantity:this.quantity,
            totalCost:this.totalCost,
            lastPrice:k[1]
          });
        }
      })
    })
    //console.log(localStorage);
    return this.getPortfolioData();
  }
  buyShares(ticker,quantity,total)
  {
    //console.log(ticker);
    this.resp=JSON.parse(localStorage[ticker]);
    //console.log(total);
    this.last=this.resp['lastPrice'];
    this.quantity=this.resp['quantity'];
    this.totalCost=this.resp['totalCost'];
    //this.totalCost=this.totalCost+total;
    // console.log(this.totalCost+total);
    this.companyName=this.resp['companyName'];
    localStorage[ticker]=JSON.stringify({
      ticker:ticker,
      companyName:this.companyName,
      quantity:quantity+this.quantity,
      totalCost:this.totalCost+total,
      lastPrice:this.last
    });
  }
  sellShares(ticker,quantity,total)
  {
    this.resp=JSON.parse(localStorage[ticker]);
    this.last=this.resp['lastPrice'];
    this.quantity=this.resp['quantity'];
    if(this.quantity-quantity==0)
    {
      localStorage.removeItem(ticker);
      return;
    }
    this.totalCost=this.resp['totalCost'];
    this.companyName=this.resp['companyName'];
    localStorage[ticker]=JSON.stringify({
      ticker:ticker,
      companyName:this.companyName,
      quantity:this.quantity-quantity,
      totalCost:(this.totalCost-total),
      lastPrice:this.last
    });
  }
}

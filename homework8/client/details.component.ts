import {Component, OnDestroy, OnInit} from '@angular/core';
import { BasicDetails } from '../BasicDetails';
import {BasicdetailsService} from '../basicdetails.service';
import { Observable} from 'rxjs';
import {ActivatedRoute} from '@angular/router'
import {switchMap,take} from 'rxjs/operators';
import {Summary} from "../Summary";
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {WatchlistServiceService} from "../watchlist-service.service";
// @ts-ignore
import {PortfolioData} from '../PortfolioData';
import {PortfolioService} from "../portfolio.service";
@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.css']
})
export class DetailsComponent implements OnInit, OnDestroy {
  myInterval: any;
  detailsdata:BasicDetails;
  ticker:string;
  color:number;
  market:boolean=false;
  portfolio:boolean=false;
  portfolioData :any={};
  watchlist:boolean=false;
  setAlert:boolean=false;
  alertStatus:boolean=false;
  isWaiting=true;
  quantity:number=0;
  total:number;
  constructor(private route: ActivatedRoute,private basicDetailsService:BasicdetailsService,private modalService :NgbModal,private watchlistService:WatchlistServiceService,private portfolioService:PortfolioService) { }
  selectStar():void
  {
    this.alertStatus=true;
    setTimeout(()=>{this.alertStatus=false;},5000);
    if(this.watchlist)
    {
      this.watchlist=false;
      this.watchlistService.removeWatchlist(this.ticker);
    }
    else {
      this.watchlist = true;
      this.watchlistService.addWatchlist(this.detailsdata);
    }
  }
  ngOnInit(): void {
    this.route.paramMap.subscribe( paramMap => { this.ticker = paramMap.get('ticker');});
    if(this.ticker=="")
      this.setAlert=true;
    if (this.watchlistService.contains(this.ticker)){
      this.watchlist =true;
    };
    this.basicDetailsService.getDetailsData(this.ticker).subscribe(res=>
    {
      this.detailsdata=res;
      if(Object.keys(res).length==0)
        this.setAlert=true;
      else
        this.setAlert=false;
    this.isWaiting=false;
    if(+res.change>=0)
      this.color=1;
    else if(+res.change<0)
      this.color=2;
    else
      this.color=0;
    if(res.marketStatus=="Close")
      this.market=false;
    else
      this.market=true;
      if(this.market===true)
      {
        this.myInterval = setInterval(() =>
        {
          this.basicDetailsService.getDetailsData(this.ticker).subscribe(res => {this.detailsdata = res;});
        }, 15000);
      }
    });
  }
  buyShares(buymodel)
  {
    this.portfolio=true;
    if(localStorage[this.detailsdata.ticker])
    {
      this.total=(this.quantity*this.detailsdata.lastPrice);
      this.portfolioService.buyShares(this.detailsdata.ticker,this.quantity,this.total);
    }
    else
    {
      this.portfolioData.ticker=this.detailsdata.ticker;
      this.portfolioData.companyName=this.detailsdata.companyName;
      this.portfolioData.quantity=this.quantity;
      this.portfolioData.totalCost=(this.quantity*this.detailsdata.lastPrice);
      this.portfolioData.lastPrice=this.detailsdata.lastPrice;
      this.portfolioService.addPortfolio(this.portfolioData);
    }
   // console.log(this.portfolioData);
    this.quantity=0;
    this.close(buymodel);
    setTimeout(()=>{this.portfolio=false;},5000);
  }
  open(buymodel) {
    this.modalService.open(buymodel);
  }
  close(buymodel)
  {
    this.modalService.dismissAll(buymodel);
  }
  ngOnDestroy():void {
    clearInterval(this.myInterval);
  }
}

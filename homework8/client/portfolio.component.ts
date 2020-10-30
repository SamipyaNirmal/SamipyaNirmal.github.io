import { Component, OnInit } from '@angular/core';
import {PortfolioService} from "../portfolio.service";
import {BasicdetailsService} from "../basicdetails.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-portfolio',
  templateUrl: './portfolio.component.html',
  styleUrls: ['./portfolio.component.css']
})
export class PortfolioComponent implements OnInit {

  constructor(private portfolioService:PortfolioService,private basicService:BasicdetailsService,private modalService :NgbModal) { }
  isLoading:boolean=true;
  data:any;
  updatedData:any;
  quantity:number=0;
  change:number=0.0;
  marketValue:number=0.0;
  ans:string;
  operation:boolean=false;
  ngOnInit(): void {
    this.ans=this.portfolioService.getPortfolioTickers();
    if(this.ans==="")
    {
      this.isLoading = false;
      this.data=this.portfolioService.getPortfolioData();
    }
    else {
      this.updatedData = this.basicService.getPortfolioUpdatedData(this.ans);
      this.basicService.getPortfolioUpdatedData(this.ans).subscribe(
        res => this.isLoading = false);
      this.data = this.portfolioService.updatePortfolioData(this.updatedData);
    }
  }
  buy(ticker,buymodel,total)
  {
    this.portfolioService.buyShares(ticker,this.quantity,total);
    this.ngOnInit();
    // this.operation=true;
    this.closeBuy(buymodel);
  }
  sell(ticker,sellmodel,total)
  {
    this.portfolioService.sellShares(ticker,this.quantity,total);
    this.ngOnInit();
    // this.operation=true;
    this.closeSell(sellmodel);
  }
  openBuy(buymodel) {
    this.modalService.open(buymodel);
  }
  closeBuy(buymodel)
  {
    this.quantity=0;
    this.modalService.dismissAll(buymodel);
  }
  openSell(sellmodel) {
    this.modalService.open(sellmodel);
  }
  closeSell(sellmodel)
  {
    this.quantity=0;
    this.modalService.dismissAll(sellmodel);
  }
}

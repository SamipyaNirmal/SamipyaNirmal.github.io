import { Component, OnInit } from '@angular/core';
import {WatchlistServiceService} from "../watchlist-service.service";
import {BasicDetails} from "../BasicDetails";
import set = Reflect.set;
import {Router} from "@angular/router";
import {BasicdetailsService} from "../basicdetails.service";

@Component({
  selector: 'app-watchlist',
  templateUrl: './watchlist.component.html',
  styleUrls: ['./watchlist.component.css']
})
export class WatchlistComponent implements OnInit {
  data:any;
  tickers:string;
  isLoading:boolean=true;
  updatedData:any;
  constructor(private watchlistService:WatchlistServiceService,private router:Router,private basicService:BasicdetailsService) { }
  ngOnInit(): void {
    this.tickers=this.watchlistService.getWatchlistTickers();
    //console.log(this.tickers);
    if(this.tickers==="")
    {
      this.isLoading = false;
      this.data=this.watchlistService.getWatchlistData();
    }
    else
    {
      this.updatedData=this.basicService.getWatchlistUpdatedData(this.tickers);
      this.basicService.getWatchlistUpdatedData(this.tickers).subscribe(
        res => this.isLoading = false
      );
      this.data=this.watchlistService.updateWatchlistData(this.updatedData);
    }
  }
  delete(ticker)
  {
    this.watchlistService.removeWatchlist(ticker);
    this.ngOnInit();
  }
  gotoDetails(ticker)
  {
    this.router.navigate(['../details', ticker]);
  }
}

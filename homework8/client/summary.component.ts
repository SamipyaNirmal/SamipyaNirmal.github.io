import { Component, OnInit } from '@angular/core';
import {BasicdetailsService} from "../../basicdetails.service";
import {ActivatedRoute} from "@angular/router";
import * as Highcharts from "highcharts/highstock";
import { Options } from "highcharts/highstock";

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  styleUrls: ['./summary.component.css']
})
export class SummaryComponent implements OnInit {
  summarydetails:any;
  color: string;
  market:boolean=false;
  ticker:string;
  myInterval:any;
  Highcharts: typeof Highcharts = Highcharts;
  chartOptions: Options = {
    title: {
      style: {
        color: 'gray'
      }
    },
    rangeSelector:{
      enabled:false
    },
    time: {
      useUTC: false
    },
    series: [
      {
        type: 'line',
        tooltip: {
          valueDecimals: 2
        }
      }
    ]
  };
  constructor(private service:BasicdetailsService, private route:ActivatedRoute) { }
  ngOnInit(): void {
    this.ticker=this.route.snapshot.params.ticker;
    this.service.getSummaryData(this.ticker).subscribe(res=> {
      this.summarydetails=res;
      this.color=this.summarydetails.color;
      if(res.marketStatus=="Close")
        this.market=false;
      else
        this.market=true;
      this.service.getDailyData(this.ticker).subscribe(res=>{
        this.chartOptions.series[0]['data']=res;
        this.chartOptions.title['text']=this.ticker;
        this.chartOptions.series[0]['color']=this.color;
        this.chartOptions.series[0]['name']=this.ticker;
      });
      if(this.market===true)
      {
         this.myInterval = setInterval(() => {
         this.service.getSummaryData(this.ticker).subscribe(res =>
         {
           this.summarydetails = res;
           this.color=this.summarydetails.color;
           if(res.marketStatus=="Close")
             this.market=false;
           else
             this.market=true;
         });
          this.service.getDailyData(this.ticker).subscribe(res=>
          {this.chartOptions.series[0]['data']=res;
          //console.log(this.chartOptions);
         this.chartOptions.series[0]['color']=this.color;
         this.chartOptions.series[0]['name']=this.ticker;});
          }, 15000);
      }
    })
  }
  ngOnDestroy():void {
    clearInterval(this.myInterval);
  }
}

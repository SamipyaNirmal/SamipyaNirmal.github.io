import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {BasicdetailsService} from "../../basicdetails.service";
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-top-news',
  templateUrl: './top-news.component.html',
  styleUrls: ['./top-news.component.css']
})
export class TopNewsComponent implements OnInit {
  ticker:string;
  data:any;
  constructor(private router:ActivatedRoute,private service:BasicdetailsService,private modalService :NgbModal) { }

  ngOnInit(): void {
    this.ticker=this.router.snapshot.params.ticker;
    this.service.getNewsData(this.ticker).subscribe(res=>this.data=res);
  }
  open(newsmodel) {
    this.modalService.open(newsmodel);
  }
  close(newsmodel)
  {
    this.modalService.dismissAll(newsmodel);
  }
}

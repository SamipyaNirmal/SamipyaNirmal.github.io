import { Component, OnInit } from '@angular/core';
import {debounceTime, tap,switchMap, finalize} from 'rxjs/operators';
import {AutoData} from '../AutoData';
import {AutocompleteService} from '../autocomplete.service';
import { FormControl } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-searchpage',
  templateUrl: './searchpage.component.html',
  styleUrls: ['./searchpage.component.css']
})
export class SearchpageComponent implements OnInit {

  searchdata:AutoData[];
  wait = false;
  searchcntrl=new FormControl();
  constructor(private autocompleteService:AutocompleteService,private router:Router) { }
  onSearchSubmit(ticker:string)
  {
    if(ticker==="")
    {
      return;
    }
    else
    this.router.navigate(['/details', ticker]);
  }
  ngOnInit(): void
  {
    this.searchcntrl.valueChanges.pipe(
    debounceTime(300),
    tap(() => {
      this.wait = true;
    }),
    switchMap((term: string) => this.autocompleteService.getData(term)))
    .subscribe((res)=>{this.searchdata = res;
      this.wait = false;
    });
  }
}

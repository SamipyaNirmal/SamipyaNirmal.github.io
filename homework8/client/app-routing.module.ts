import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DetailsComponent } from './details/details.component';
import { PortfolioComponent } from './portfolio/portfolio.component';
import { SearchpageComponent } from './searchpage/searchpage.component';
import { WatchlistComponent } from './watchlist/watchlist.component';

const routes: Routes = [
  {path:'',component:SearchpageComponent},
  {path:'watchlist',component:WatchlistComponent},
  {path:'portfolio',component:PortfolioComponent},
  {path:'details/:ticker',component:DetailsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

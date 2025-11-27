import { Component, OnInit } from '@angular/core';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import Trip from '../../../models/trip.model';
import { TripService } from '../../../service/trip.service';

@Component({
  selector: 'app-trip-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './list-trips.html',
  styleUrl: './list-trips.css',
})
export class TripListComponent implements OnInit{
  public selectedTrip!: Trip
  public selectedIndex: number =0
  public tripList!: Trip[]
  public savedUser!:JSON

constructor(private route:ActivatedRoute, private router: Router, private _tripService:TripService){}

ngOnInit(): void {
  const savedUserStr=localStorage.getItem('user');
  if(savedUserStr){
   const savedUser=JSON.parse(savedUserStr);
   console.log('Saved in a localStorge: ',savedUser) 
   var id:number;
   this.route.params.subscribe((params)=>{
     id= params['id']
     if(id==-1){
       this._tripService.getTripsByUserId(savedUser.id).subscribe({
         next:(res) =>{
             console.log(res)
      this.tripList=res;
    },
    error:(err ) =>{
      console.log(err)
    }
  })
}
else{
  this._tripService.getTripsByCategoryId(id,savedUser.id).subscribe({
    
      next:(res) =>{
         console.log(res)
         this.tripList = res;
       },
      error:(err ) =>{
      console.log(err)
   }
 }) 
}

   })
  }
}
showDetails(Trip: Trip){
  this.router.navigate(['/Trip-details',Trip.id])
}
addNewTrip(){
  this.router.navigate(['/add-Trip'])
}
nextTrip(){
  this.selectedIndex++;
  if(this.selectedIndex<this.tripList.length){
    this.selectedTrip=this.tripList[this.selectedIndex]
  }else{
      this.selectedIndex = 0;
      this.selectedTrip = this.tripList[this.selectedIndex]
  }
}

  goPreviousPage(){
  this.router.navigate(['/welcome'])
}
}

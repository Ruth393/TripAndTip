import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TripService } from '../../../service/trip.service';
import Trip,{ TripToUpload} from '../../../models/trip.model';
import { ActivatedRoute, Router ,RouterModule} from '@angular/router';
import Category from '../../../models/category.model';
import { CategoryService } from '../../../service/category.service';
@Component({
  selector: 'app-trip-details',
  standalone: true,
  imports: [FormsModule,RouterModule],
  templateUrl: './trip-details.html',
  styleUrl: './trip-details.css',
})
export class TripDetails implements OnInit {
public TripToShow!: Trip

public isUpdateTask: boolean=false
  
  constructor(private router: Router,private route: ActivatedRoute, private _TripService: TripService) { }

  ngOnInit(): void {
    var id: number
    this.route.params.subscribe((params)=>{
      id=params['id']
  
      this._TripService.getTripById(id).subscribe({
      next: (res) => {
        this.TripToShow = res
        console.log(this.TripToShow);

      },
      error: (err) => {
        console.log(err);

      }
    })
    })
  }


  close(){
    this.router.navigate(['/list-trips/-1'])
  }


  goPreviousPage(){
  this.router.navigate(['/welcome'])
}
}

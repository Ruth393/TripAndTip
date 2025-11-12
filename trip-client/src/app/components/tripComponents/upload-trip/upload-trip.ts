import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TripService } from '../../../service/trip.service';
import Trip,{ TripToAdd} from '../../../models/trip.model';
import { Router ,RouterModule} from '@angular/router';
import Category from '../../../models/category.model';
import { CategoryService } from '../../../service/category.service';

@Component({
  selector: 'app-upload-trip',
  standalone: true,
  imports: [FormsModule,RouterModule],
  templateUrl: './upload-trip.html',
  styleUrl: './upload-trip.css',
})
export class UploadTrip implements OnInit {
  public categoryiesList!:Category[]
  public newTrip: TripToAdd = {
    name: "",
    description: "",
    cost: "",
    match: "",
    users: {id:0},
    category: {id:0}
  }
  constructor(
    private router: Router,
    public _tripService: TripService, 
    private _categoryService: CategoryService 
  ) {}

  ngOnInit(): void {
    this._categoryService.getCategorysFromServer().subscribe({
        next:(res)=>{
          this.categoryiesList=res
        },
        error:(err)=>{
          console.log(err)
        }
    });
  }

  addTrip() {
    this._tripService.addTrip(this.newTrip).subscribe({
      next: (res) => {
        console.log(res);
        this.router.navigate(['/list-trips/-1']);
      },
      error: (err) => {
        console.log(err);
      }
    });
  }
  goPreviousPage(){
    this.router.navigate(['welcome'])
  }
}

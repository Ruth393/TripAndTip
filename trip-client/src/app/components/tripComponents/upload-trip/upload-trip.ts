import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TripService } from '../../../service/trip.service';
import { CategoryService } from '../../../service/category.service';
import { TripToUpload } from '../../../models/trip.model';  // ← בדיוק כמו שלך
import Category from '../../../models/category.model';

@Component({
  selector: 'app-upload-trip',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './upload-trip.html',
  styleUrl: './upload-trip.css',
})
export class UploadTrip implements OnInit {
  categoryiesList: Category[] = [];
  selectedFile: File | null = null;

  // ← בדיוק כמו שהמודל שלך מגדיר – בלי שינוי!
  newTrip: TripToUpload = {
    name: '',
    description: '',
    cost: '',
    match: '',
    users: { id: 0 },           // אם את לא שולחת – תשאירי ככה או תורידי
    category: { id: 0 }
  };

  constructor(
    private router: Router,
    private _tripService: TripService,
    private _categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this._categoryService.getCategorysFromServer().subscribe(res => {
      this.categoryiesList = res;
    });
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0] || null;
  }

  uploadTrip(form?: NgForm): void {
    // validate form if provided
    if (form && form.invalid) {
      alert('אנא מלא/י את השדות החסרים/תקינים לפני שליחה');
      return;
    }

    if (!this.newTrip.name || this.newTrip.name.length < 5) {
      alert('Name required (min 5 chars)');
      return;
    }

    if (!this.selectedFile) {
      alert('חייב לבחור תמונה!');
      return;
    }

    if (!this.newTrip.category || !this.newTrip.category.id || this.newTrip.category.id === 0) {
      alert('בחרי קטגוריה לפני העלאה');
      return;
    }

    const formData = new FormData();
    // שליחת השדות כרשומה JSON יחד עם הקובץ
    formData.append('trip', JSON.stringify(this.newTrip));
    formData.append('image', this.selectedFile, this.selectedFile.name);

    this._tripService.uploadTrip(formData).subscribe({
      next: (res) => {
        console.log('הטיול הועלה בהצלחה!', res);
        this.router.navigate(['/list-trips/-1']);
      },
      error: (err) => {
        console.error('שגיאה:', err);
        alert('נכשל');
      }
    });
  }

  goPreviousPage(): void {
    window.history.back();
  }
}
// import { Component, OnInit } from '@angular/core';
// import { FormsModule } from '@angular/forms';
// import { TripService } from '../../../service/trip.service';
// import { TripToUpload} from '../../../models/trip.model';
// import { Router ,RouterModule} from '@angular/router';
// import Category from '../../../models/category.model';
// import { CategoryService } from '../../../service/category.service';

// @Component({
//   selector: 'app-upload-trip',
//   standalone: true,
//   imports: [FormsModule,RouterModule],
//   templateUrl: './upload-trip.html',
//   styleUrl: './upload-trip.css',
// })
// export class UploadTrip implements OnInit {
//   public categoryiesList!:Category[]
//   public newTrip: TripToUpload = {
//     name: "",
//     description: "",
//     cost: "",
//     match: "",
//     users: {id:0},
//     category: {id:0}
//   }
//   constructor(
//     private router: Router,
//     public _tripService: TripService, 
//     private _categoryService: CategoryService 
//   ) {}

//   ngOnInit(): void {
//     this._categoryService.getCategorysFromServer().subscribe({
//         next:(res)=>{
//           this.categoryiesList=res
//         },
//         error:(err)=>{
//           console.log(err)
//         }
//     });
//   }

//   uploadTrip() {
//     this._tripService.uploadTrip(this.newTrip).subscribe({
//       next: (res) => {
//         console.log(res);
//         this.router.navigate(['/list-trips/-1']);
//       },
//       error: (err) => {
//         console.log(err);
//       }
//     });
//   }
//   goPreviousPage(){
//     this.router.navigate(['welcome'])
//   }
// }
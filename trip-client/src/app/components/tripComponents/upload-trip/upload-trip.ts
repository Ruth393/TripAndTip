import { Component, OnInit, ViewChild, ElementRef } from '@angular/core'; // ← ״הוספתי ViewChild ו-ElementRef
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../../../material.module';
import { TripService } from '../../../service/trip.service';
import { CategoryService } from '../../../service/category.service';
import { UserService } from '../../../service/user.service';
import { TripToUpload } from '../../../models/trip.model';
import Category from '../../../models/category.model';
import { AuthResponse } from '../../../models/user.model';

@Component({
  selector: 'app-upload-trip',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, MaterialModule],
  templateUrl: './upload-trip.html',
  styleUrl: './upload-trip.css',
})
export class UploadTrip implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>; // ← חדש: גישה לשדה הקלט

  categoriesList: Category[] = [];
  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;
  isLoading: boolean = false;
  errorMessage: string = '';

  // store the user that is currently logged in
  currentUser: AuthResponse | null = null;

  newTrip: TripToUpload = {
    name: '',
    description: '',
    cost: 0,
    match: '',
    users: { id: 0 },
    category: { id: 0 },
  };

  isUserLoggedIn = false;
  pendingUpload = false;

  constructor(
    private router: Router,
    private _tripService: TripService,
    private _categoryService: CategoryService,
    private _userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadCategories();

    // listen for current user changes; keep id for upload
    this._userService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
        this.isUserLoggedIn = !!user?.id;
        if (this.isUserLoggedIn && user?.id) {
          // fill the trip object with the logged‑in user's id
          this.newTrip.users.id = user.id;
        }

        // if we were waiting for login and returned back, immediately attempt upload
        if (this.isUserLoggedIn && this.pendingUpload) {
          this.pendingUpload = false; // clear state so we don't loop
          this.uploadTrip();
        }
      },
      error: () => {
        this.isUserLoggedIn = false;
      },
    });
  }

  loadCategories(): void {
    this._categoryService.getCategoriesFromServer().subscribe((res) => {
      this.categoriesList = res;
    });
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0] || null;

    if (this.selectedFile) {
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result;
      };
      reader.readAsDataURL(this.selectedFile);
    } else {
      this.imagePreview = null;
    }
  }

  // ← חדש: פונקציה להפעלת לחיצה על שדה הקלט
  triggerFileInput(): void {
    if (this.fileInput) {
      this.fileInput.nativeElement.click();
    }
  }

  uploadTrip(form?: NgForm): void {
    if (form && form.invalid) {
      this.errorMessage = 'אנא מלא את כל השדות הנדרשים';
      return;
    }

    // basic form validation – name, image and category are mandatory
    if (!this.newTrip.name || !this.selectedFile || !this.newTrip.category?.id) {
      this.errorMessage = 'שם, תמונה וקטגוריה נדרשים';
      return;
    }

    if (!this.isUserLoggedIn) {
      // user must login before uploading, redirect to sign-in page
      this.errorMessage = 'עליך להתחבר כדי לשלוח את הטיול';
      this.router.navigate(['/sign-in']);
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.pendingUpload = false;

    const formData = new FormData();
    // include logged‑in user id if available (backend may need it)
    const tripObject: any = {
      name: this.newTrip.name,
      description: this.newTrip.description,
      cost: this.newTrip.cost,
      match: this.newTrip.match,
      category: { id: this.newTrip.category.id }
    };
    if (this.currentUser?.id) {
      tripObject.users = { id: this.currentUser.id };
    }

    formData.append('trip', new Blob([JSON.stringify(tripObject)], { type: 'application/json' }));
    formData.append('image', this.selectedFile, this.selectedFile.name);

    this._tripService.uploadTrip(formData).subscribe({
next: () => {
  this.isLoading = false;
  this.router.navigate(['/list-trips'], { skipLocationChange: false })
    .then(() => {
      this.router.navigate(['/list-trips']);
    });
},
      error: (err) => {
        this.isLoading = false;
        console.error('Upload error:', err);
        this.errorMessage = 'שגיאה בשליחת הטיול. נסה שוב.';
      },
    });
  }

  goPreviousPage(): void {
    window.history.back();
  }
}
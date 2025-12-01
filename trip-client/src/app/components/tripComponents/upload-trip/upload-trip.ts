import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '.././../../material.module';
import { TripService } from '../../../service/trip.service';
import { CategoryService } from '../../../service/category.service';
import { TripToUpload } from '../../../models/trip.model';
import Category from '../../../models/category.model';

@Component({
  selector: 'app-upload-trip',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, MaterialModule],
  templateUrl: './upload-trip.html',
  styleUrl: './upload-trip.css',
})
export class UploadTrip implements OnInit {
  categoryiesList: Category[] = [];
  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null; // Base64 preview

  newTrip: TripToUpload = {
    name: '',
    description: '',
    cost: '',
    match: '',
    users: { id: 0 },
    category: { id: 0 },
  };

  constructor(
    private router: Router,
    private _tripService: TripService,
    private _categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this._categoryService.getCategorysFromServer().subscribe((res) => {
      this.categoryiesList = res;
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

  uploadTrip(form?: NgForm): void {
    if (form && form.invalid) return;
    if (!this.newTrip.name || !this.selectedFile || !this.newTrip.category?.id) return;
    
    const formData = new FormData();
    formData.append('trip', JSON.stringify(this.newTrip));
    formData.append('image', this.selectedFile, this.selectedFile.name);

    this._tripService.uploadTrip(formData).subscribe({
      next: () => this.router.navigate(['/list-trips']),
      error: () => console.log("נכשל בהעלאה😞"),
    });
  }

  goPreviousPage(): void {
    window.history.back();
  }
}

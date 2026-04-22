// src/app/components/trip-list/list-trips.component.ts

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import TripDTO, { TripListDTO } from '../../../models/trip.model';
import Category from '../../../models/category.model';
import { TripService } from '../../../service/trip.service';
import { UserService } from '../../../service/user.service';
import { MaterialModule } from '../../../material.module';
import { CategoryService } from '../../../service/category.service';

@Component({
  selector: 'app-trip-list',
  standalone: true,
  imports: [FormsModule, RouterModule, CommonModule, MaterialModule],
  templateUrl: './list-trips.html',
  styleUrls: ['./list-trips.css']
})
export class TripListComponent implements OnInit {

  public tripList: TripListDTO[] = [];
  public categories: Category[] = [];
  public selectedCategoryId: number | null = null;

  public loading = false;
  public error: string | null = null;

  constructor(
    private router: Router,
    private _tripService: TripService,
    private _userService: UserService,
    private _categoryService: CategoryService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadTrips();
    this.loadCategories();

    if (typeof window !== 'undefined' && window.sessionStorage) {
      const hasReloaded = sessionStorage.getItem('hasReloaded');
      if (!hasReloaded) {
        sessionStorage.setItem('hasReloaded', 'true');
        window.location.reload();
        return;
      }
    }
  }

  loadTrips(): void {
    this.loading = true;
    this._tripService.getTrips().subscribe({
      next: (res) => {
        this.tripList = res || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.tripList = [];
        this.error = 'Failed to load trips';
        this.loading = false;
      }
    });
  }

  loadCategories(): void {
    this._categoryService.getCategoriesFromServer().subscribe({
      next: (res) => this.categories = res || [],
      error: (err) => console.error('Failed to load categories', err)
    });
  }

  filterTripsByCategory(): void {
    if (!this.selectedCategoryId) {
      this.loadTrips();
      return;
    }

    this.loading = true;
    this._tripService.getTripsByCategoryId([this.selectedCategoryId]).subscribe({
      next: (res) => {
        this.tripList = res || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.tripList = [];
        this.loading = false;
      }
    });
  }

  loadTripsByCurrentUser(): void {
    this.loading = true;
    this._userService.getCurrentUser().subscribe({
      next: (user) => {
        const userId = (user as any)?.id;
        if (!userId) {
          this.tripList = [];
          this.loading = false;
          return;
        }
        this._tripService.getTripsByUserId(userId).subscribe({
          next: (res) => {
            this.tripList = res || [];
            this.loading = false;
            this.cdr.detectChanges();
          },
          error: () => {
            this.tripList = [];
            this.error = 'Failed to load trips by user';
            this.loading = false;
          }
        });
      },
      error: () => {
        this.tripList = [];
        this.error = 'Failed to get current user';
        this.loading = false;
      }
    });
  }

  showDetails(trip: TripListDTO) {
    this.router.navigate(['/trip-details', trip.id], { state: { trip } });
  }

  addNewTrip() {
    this.router.navigate(['/upload-trip']);
  }

  getImageSrc(trip: TripListDTO): string {
    const raw = trip.image || trip.imagePath;
    if (!raw) return 'https://via.placeholder.com/400x200?text=No+Image';
    if (raw.startsWith('/9j/') || raw.startsWith('iVBORw0KGgo')) {
      return 'data:image/jpeg;base64,' + raw;
    }
    return `http://localhost:8080/images/${raw}`;
  }

// ⭐ פונקציה מתוקנת כדי לזהות Base64 או ImagePath
getUserAvatarSrc(trip: TripListDTO): string {
    const user = trip.user;
    if (!user || (!user.image && !user.imagePath)) return 'assets/A1.png'; // ברירת מחדל

    // 1. בדיקת Base64 (user.image)
    if (user.image) {
        if (user.image.startsWith('http')) {
            return user.image;
        }
        if (user.image.startsWith('data:')) {
            return user.image;
        }
        if (user.image.startsWith('/9j/') || user.image.startsWith('iVBORw')) {
             return 'data:image/jpeg;base64,' + user.image;
        }
    }

    // 2. בדיקת נתיב שרת (user.imagePath)
    if (user.imagePath) {
      return `http://localhost:8080/images/${user.imagePath}`;
    }

    return 'assets/A1.png'; // ברירת מחדל
  }
}
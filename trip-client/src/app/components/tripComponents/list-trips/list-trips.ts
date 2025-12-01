import { Component, OnInit } from '@angular/core';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import Trip, { TripListDTO } from '../../../models/trip.model';
import { TripService } from '../../../service/trip.service';
import { UserService } from '../../../service/user.service';
import { MaterialModule } from '../../../material.module';
@Component({
  selector: 'app-trip-list',
  standalone: true,
  imports: [CommonModule, RouterModule,MaterialModule],
  templateUrl: './list-trips.html',
  styleUrl: './list-trips.css',
})
export class TripListComponent implements OnInit {
  public selectedTrip!: TripListDTO;
  public selectedIndex: number = 0;
  public tripList!: TripListDTO[];
  public loading: boolean = false;
  public error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private _tripService: TripService,
    private _userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadAllTrips();
  }

  getImageSrc(trip: TripListDTO): string | null {
    const raw = (trip as any).image || (trip as any).imageUrl || (trip as any).imagePath;
    if (!raw || typeof raw !== 'string') return null;

    const trimmed = raw.trim();
    if (trimmed.startsWith('data:') || trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
      return trimmed;
    }
    return 'data:image/jpeg;base64,' + trimmed;
  }

  loadAllTrips(): void {
    this.loading = true;
    this._tripService.getTrips().subscribe({
      next: (res) => {
        this.tripList = res || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load trips', err);
        this.tripList = [];
        this.error = 'Failed to load trips from server.';
        this.loading = false;
      }
    });
  }

  loadTripsByCategories(categoryIds: number[]): void {
    this.loading = true;
    this._tripService.getTripsByCategories(categoryIds).subscribe({
      next: (res) => {
        this.tripList = res || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load trips by categories', err);
        this.tripList = [];
        this.error = 'Failed to load trips by categories.';
        this.loading = false;
      }
    });
  }

  loadTripsByCurrentUser(): void {
    this.loading = true;
    this._userService.getCurrentUser().subscribe({
      next: (user) => {
        const userId = (user as any)?.id;
        if (userId) {
          this._tripService.getTripsByUserId(userId).subscribe({
            next: (res) => {
              this.tripList = res || [];
              this.loading = false;
            },
            error: (err) => {
              console.error('Failed to load trips by user', err);
              this.tripList = [];
              this.error = 'Failed to load trips by user.';
              this.loading = false;
            }
          });
        } else {
          this.tripList = [];
          this.loading = false;
        }
      },
      error: (err) => {
        console.error('Failed to get current user', err);
        this.tripList = [];
        this.loading = false;
      }
    });
  }

  showDetails(trip: TripListDTO) {
    this.router.navigate(['/trip-details', trip.id]);
  }

  addNewTrip() {
    this.router.navigate(['/upload-trip']);
  }

  nextTrip() {
    this.selectedIndex++;
    if (this.selectedIndex < this.tripList.length) {
      this.selectedTrip = this.tripList[this.selectedIndex];
    } else {
      this.selectedIndex = 0;
      this.selectedTrip = this.tripList[this.selectedIndex];
    }
  }

  goPreviousPage() {
    this.router.navigate(['/welcome']);
  }
}

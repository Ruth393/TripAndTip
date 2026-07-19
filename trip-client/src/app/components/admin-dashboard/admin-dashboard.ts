import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TripService } from '../../service/trip.service';
import { UserService } from '../../service/user.service';
import { TripListDTO } from '../../models/trip.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})
export class AdminDashboard implements OnInit {
  stats = { totalTrips: 0, totalUsers: 0 };
  isAdmin = false;
  error: string | null = null;
  trips: TripListDTO[] = [];
  isLoading = true;

  constructor(
    private tripService: TripService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe(user => {
      const currentUser = user ?? null;
      const roles = Array.isArray(currentUser?.roles) ? currentUser.roles : [];
      const authorities = Array.isArray(currentUser?.authorities) ? currentUser.authorities : [];
      const isCurrentAdmin = Boolean(currentUser) && (currentUser?.isAdmin === true || roles.includes('ROLE_ADMIN') || authorities.includes('ROLE_ADMIN'));
      this.isAdmin = isCurrentAdmin;
      if (!this.isAdmin) {
        this.error = 'אין לך הרשאה לצפות בדף זה.';
        this.isLoading = false;
        return;
      }
      this.loadStats();
      this.loadTrips();
    });
  }

  private loadStats(): void {
    this.tripService.getDashboardStats().subscribe({
      next: stats => {
        this.stats = stats;
      },
      error: err => {
        console.error('Failed loading admin stats', err);
        this.error = 'שגיאה בטעינת הנתונים. נסה שוב מאוחר יותר.';
      }
    });
  }

  private loadTrips(): void {
    this.tripService.getTrips().subscribe({
      next: trips => {
        this.trips = trips || [];
        this.isLoading = false;
      },
      error: err => {
        console.error('Failed loading trips for admin', err);
        this.error = 'לא ניתן לטעון את הטיולים כרגע.';
        this.isLoading = false;
      }
    });
  }

  deleteTrip(tripId: number): void {
    const confirmed = window.confirm('האם למחוק את הטיול הזה לצמיתות?');
    if (!confirmed) {
      return;
    }

    this.tripService.deleteTripByAdmin(tripId).subscribe({
      next: () => {
        this.trips = this.trips.filter(trip => trip.id !== tripId);
        this.stats.totalTrips = Math.max(0, this.stats.totalTrips - 1);
      },
      error: err => {
        console.error('Failed deleting trip', err);
        this.error = 'המחיקה נכשלה. נסה שוב אחר כך.';
      }
    });
  }

  formatCategory(category: any): string {
    return category?.name || category?.category || 'לא הוגדר';
  }
}

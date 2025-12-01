import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TripService } from '../../../service/trip.service';
import  Trip,{ TripToUpload } from '../../../models/trip.model';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CategoryService } from '../../../service/category.service';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-trip-details',
  standalone: true,
  imports: [FormsModule, RouterModule, CommonModule],
  templateUrl: './trip-details.html',
  styleUrl: './trip-details.css',
})
export class TripDetails implements OnInit, OnDestroy {
  public tripToShow!: Trip;
  public isUpdateTask: boolean = false;
  public isLoading: boolean = true;
  public errorMessage: string | null = null;
  
  private destroy$ = new Subject<void>();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private tripService: TripService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.route.params
      .pipe(takeUntil(this.destroy$))
      .subscribe((params) => {
        const id: number = params['id'];
        this.loadTrip(id);
      });
  }

  private loadTrip(id: number): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.tripService
      .getTripById(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res) => {
          this.tripToShow = res;
          this.isLoading = false;
          console.log('Trip loaded:', this.tripToShow);
        },
        error: (err) => {
          this.errorMessage = 'Failed to load trip details';
          this.isLoading = false;
          console.error('Error loading trip:', err);
        }
      });
  }

  public getSafeImageUrl(base64Data: string): SafeUrl {
    if (!base64Data) {
      return this.sanitizer.bypassSecurityTrustUrl('assets/placeholder.png');
    }
    const dataUri = base64Data.startsWith('data:') 
      ? base64Data 
      : `data:image/jpeg;base64,${base64Data}`;
    return this.sanitizer.bypassSecurityTrustUrl(dataUri);
  }

  public close(): void {
    this.router.navigate(['/list-trips']);
  }

  public goPreviousPage(): void {
    this.router.navigate(['/welcome']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
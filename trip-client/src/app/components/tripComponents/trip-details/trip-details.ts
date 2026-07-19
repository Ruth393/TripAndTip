// src/app/components/trip-details/trip-details.component.ts
import { Component, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TripService } from '../../../service/trip.service';
import Trip from '../../../models/trip.model';
import { ListComments } from '../../commentComponents/list-comments/list-comments';
import { AddComment } from '../../commentComponents/add-comment/add-comment';
import { CommentDTO } from '../../../models/comment.model';
import { PackingListComponent } from '../packing-list-component/packing-list-component';
import { ChatBox } from '../chat-box/chat-box';
import { UserService } from '../../../service/user.service';

@Component({
  selector: 'app-trip-details',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    ListComments, 
    AddComment,
    PackingListComponent,
    ChatBox
],
  templateUrl: './trip-details.html',
  styleUrls: ['./trip-details.css']
})
export class TripDetails implements OnInit {

  @ViewChild('listCommentsRef') listCommentsRef!: ListComments;

  TripToShow?: Trip;
  showAddForm = false;
  commentsCount = 0; 

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tripService: TripService,
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe(user => {
      if (user?.id) {
        this.updateTripOwnerImage(user);
      }
    });

    const id = +this.route.snapshot.paramMap.get('id')!; 
    this.tripService.getTripById(id).subscribe({
      next: (trip) => {
        this.TripToShow = trip;
        const currentUser = this.userService['currentUserSubject']?.value;
        if (currentUser?.id) {
          this.updateTripOwnerImage(currentUser);
        }
        this.cdr.detectChanges();
      }
    });
  }

  updateTripOwnerImage(updatedUser: { id?: number; image?: string; imagePath?: string; imageUrl?: string }): void {
    if (!this.TripToShow?.user || !updatedUser?.id || this.TripToShow.user.id !== updatedUser.id) {
      return;
    }

    this.TripToShow = {
      ...this.TripToShow,
      user: {
        ...this.TripToShow.user,
        image: updatedUser.image ?? this.TripToShow.user.image,
        imagePath: updatedUser.imagePath ?? this.TripToShow.user.imagePath,
        imageUrl: updatedUser.imageUrl ?? this.TripToShow.user.imageUrl
      }
    };
  }

  onCommentsCountChange(count: number) {
    this.commentsCount = count;
  }

  onCommentAdded(comment: CommentDTO) {
    this.showAddForm = false;
    // לוודא ש-TripToShow.id קיים
    if (this.TripToShow?.id) { 
        this.listCommentsRef.addComment(comment);
    }
  }

  goPreviousPage() {
    this.router.navigate(['/list-trips']);
  }

  // פונקציות עזר לתמונות נשארות זהות
  getTripImage(): string {
    if (!this.TripToShow) return 'https://via.placeholder.com/800x400';
    if (this.TripToShow.image?.startsWith('/9j/') || this.TripToShow.image?.startsWith('iVBORw')) {
      return 'data:image/jpeg;base64,' + this.TripToShow.image;
    }
    if (this.TripToShow.imagePath) {
      return `http://localhost:8080/images/${this.TripToShow.imagePath}`;
    }
    return 'https://via.placeholder.com/800x400?text=No+Image';
  }

  getUserImage(): string {
    const user = this.TripToShow?.user;
    if (!user) return 'assets/A1.png';

    const img = user.image || user.imagePath || user.imageUrl;
    if (!img) return 'assets/A1.png';

    if (typeof img === 'string' && (img.startsWith('http') || img.startsWith('data:'))) {
      return img;
    }

    if (img.startsWith('iVBORw') || img.startsWith('/9j/')) {
      return 'data:image/png;base64,' + img;
    }

    return `http://localhost:8080/images/${img}`;
  }
}
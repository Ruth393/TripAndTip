import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../material.module';
import { UserService } from '../service/user.service';
import { AsyncPipe } from '@angular/common';
import { CommonModule } from '@angular/common';
import { MatMenuTrigger } from '@angular/material/menu';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule, MaterialModule, AsyncPipe,CommonModule],  // ← רק AsyncPipe! בלי CommonModule בכלל
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  private userService = inject(UserService);
  user$ = this.userService.currentUser$;

  // if the user selects a new picture we show it immediately while waiting for
  // the server to respond; stored as a data URL so it works regardless of
  // backend implementation.
  previewUrl: string | null = null;

  // timer used for delayed closing of the profile menu/overlay
  private closeTimer: any;

  signOut() {
    this.userService.signOut().subscribe();
  }

  /**
   * menu trigger handlers; keeps overlay open for a short duration when the
   * mouse leaves so the user can move the pointer into the panel without it
   * immediately disappearing.  also allows opening on click.
   */
  onProfileMouseEnter(trigger: MatMenuTrigger) {
    if (this.closeTimer) {
      clearTimeout(this.closeTimer);
      this.closeTimer = null;
    }
    trigger.openMenu();
  }

  onProfileMouseLeave(trigger: MatMenuTrigger) {
    // wait a couple of seconds before closing so user can move cursor
    this.closeTimer = setTimeout(() => trigger.closeMenu(), 2000);
  }

  onProfileClick(trigger: MatMenuTrigger) {
    // clicking toggles, but we also clear any pending close timer
    if (this.closeTimer) {
      clearTimeout(this.closeTimer);
      this.closeTimer = null;
    }
    trigger.toggleMenu();
  }

getProfileImage(user: any): string {
  if (!user?.image) return 'assets/A1.png';

  if (user.image.startsWith('http')) {
    return user.image + '?v=' + new Date().getTime();
  }
  
  if (user.image.startsWith('data:')) {
    return user.image;
  }
  return 'data:image/jpeg;base64,' + user.image;
}

  onLogoError(event: Event) {
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = 'assets/Logo1.png'; 
  }

  /**
   * נבחרה תמונה חדשה עבור הפרופיל – נשלח לשרת ונעדכן ב‑BehaviorSubject
   */
  onProfileImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      return;
    }
    const file = input.files[0];

    // show preview immediately
    const reader = new FileReader();
    reader.onload = () => {
      this.previewUrl = reader.result as string;
    };
    reader.readAsDataURL(file);

    // נבנה FormData בנוסח ההרשמה
    const formData = new FormData();
    formData.append('image', file, file.name);

    // הבקשה תזוהה ב‑user.service
    this.userService.updateProfile(formData).subscribe({
      next: (updated) => {
        // the server returns the new user object; if for some reason it doesn't
        // include the image we still have previewUrl. once the BehaviorSubject
        // emits the update, previewUrl can be cleared to avoid stale data.
        this.previewUrl = null;
      },
      error: (err) => {
        console.error('שגיאה בעדכון תמונת פרופיל', err);
        // keep preview even on error so the user sees the selected image
      }
    });

    // נקה את הקלט כדי לאפשר בחירה חוזרת באותו קובץ
    input.value = '';
  }

}
import { Component, inject, OnInit } from '@angular/core';
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
export class HeaderComponent implements OnInit {
  private userService = inject(UserService);
  user$ = this.userService.currentUser$;
  isDarkMode = false;
  logoLight = 'assets/logo.png';
  logoDark = 'assets/logoW.png';

  ngOnInit(): void {
    const savedTheme = typeof window !== 'undefined' ? window.localStorage.getItem('app-theme') : null;
    this.isDarkMode = savedTheme === 'dark';
    this.applyTheme();
  }

  get logoSrc(): string {
    return this.isDarkMode ? this.logoDark : this.logoLight;
  }

  toggleTheme(): void {
    this.isDarkMode = !this.isDarkMode;
    this.applyTheme();
  }

  isAdminUser(user: any): boolean {
    return !!user && (user.isAdmin || user.roles?.includes('ROLE_ADMIN') || user.authorities?.includes('ROLE_ADMIN'));
  }

  private applyTheme(): void {
    const root = typeof document !== 'undefined' ? document.body : null;
    if (!root) {
      return;
    }

    root.classList.toggle('dark-theme', this.isDarkMode);
    root.classList.toggle('light-theme', !this.isDarkMode);

    if (typeof window !== 'undefined') {
      window.localStorage.setItem('app-theme', this.isDarkMode ? 'dark' : 'light');
    }
  }

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

getProfileImage(user: any): string | null {
  const imageValue = user?.image || user?.imageUrl || user?.imagePath;
  if (!imageValue || typeof imageValue !== 'string') {
    return null;
  }

  const normalized = imageValue.trim();
  if (!normalized) {
    return null;
  }

  if (normalized.startsWith('http')) {
    return normalized + '?v=' + new Date().getTime();
  }

  if (normalized.startsWith('data:')) {
    return normalized;
  }

  if (normalized.startsWith('/9j/') || normalized.startsWith('iVBORw')) {
    return 'data:image/jpeg;base64,' + normalized;
  }

  return `http://localhost:8080/images/${normalized}`;
}

hasProfileImage(user: any): boolean {
  return Boolean(this.previewUrl || this.getProfileImage(user));
}

getAvatarInitial(user: any): string {
  const name = String(user?.name || user?.userName || user?.email || '').trim();
  const firstLetter = name.match(/[A-Za-z]/)?.[0];
  return firstLetter ? firstLetter.toUpperCase() : '?';
}

getAvatarColor(user: any): string {
  const seed = String(user?.id ?? user?.email ?? user?.name ?? user?.userName ?? 'user').toLowerCase();
  let hash = 0;

  for (let i = 0; i < seed.length; i++) {
    hash = seed.charCodeAt(i) + ((hash << 5) - hash);
  }

  const hue = Math.abs(hash % 360);
  return `hsl(${hue} 70% 45%)`;
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
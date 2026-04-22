import { Component, Input, OnInit, OnChanges, SimpleChanges, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatService } from '../../../service/chat.service';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-packing-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './packing-list-component.html',
  styleUrls: ['./packing-list-component.css']
})
export class PackingListComponent implements OnInit, OnChanges, OnDestroy {
  @Input() tripId!: number;

  packingListRaw: string = '';
  errorMessage: string | null = null;
  isLoading: boolean = false;

  private packingSub: Subscription | null = null;

  constructor(
    private chatService: ChatService,
    private sanitizer: DomSanitizer,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (this.tripId) {
      this.loadPackingList();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['tripId'] && !changes['tripId'].isFirstChange()) {
      if (this.tripId) {
        this.loadPackingList();
      }
    }
  }

  ngOnDestroy(): void {
    this.cancelSubscription();
  }

  private cancelSubscription(): void {
    if (this.packingSub) {
      this.packingSub.unsubscribe();
      this.packingSub = null;
    }
  }

  /**
   * ממיר טקסט גולמי (Markdown-like) ל-HTML בסיסי
   * כדי שהתוכן יוצג בצורה נכונה ב-innerHTML
   */
  private convertToHtml(raw: string): string {
    if (!raw) return '';

    return raw
      // כותרות ## → <h4>
      .replace(/^## (.+)$/gm, '<h4>$1</h4>')
      // כותרות # → <h3>
      .replace(/^# (.+)$/gm, '<h3>$1</h3>')
      // bold **text** → <strong>
      .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
      // פריטי רשימה - item → <li>
      .replace(/^[-*] (.+)$/gm, '<li>$1</li>')
      // עוטף רצפים של <li> ב-<ul>
      .replace(/(<li>.*<\/li>\n?)+/gs, match => `<ul>${match}</ul>`)
      // שורות ריקות → מעבר פסקה
      .replace(/\n{2,}/g, '<br/><br/>')
      // שורה רגילה → מעבר שורה
      .replace(/\n/g, '<br/>');
  }

  get safePackingListHtml(): SafeHtml {
    const html = this.convertToHtml(this.packingListRaw);
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }

  loadPackingList(): void {
    if (!this.tripId) return;

    this.packingListRaw = '';
    this.errorMessage = null;
    this.isLoading = true;

    this.cancelSubscription();

    this.packingSub = this.chatService.getPackingListStream(this.tripId).subscribe({
      next: chunk => {
        this.isLoading = false;
        this.packingListRaw += chunk;
        // ensure Angular runs change detection since fetch runs outside Angular zone
        this.cdr.detectChanges();
      },
      error: err => {
        this.isLoading = false;
        console.error('Packing list error:', err);
        this.errorMessage = 'שגיאה בטעינת רשימת ציוד. אנא נסה שנית.';
        this.cdr.detectChanges();
      },
      complete: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
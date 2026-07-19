// src/app/service/chat.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { NgZone } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private baseUrl = 'http://localhost:8080/api/trip';

  constructor(private http: HttpClient, private zone: NgZone) {}

  /**
   * קבלת רשימת ציוד ב-Streaming (SSE) באמצעות Fetch API.
   * @param tripId ה-ID של הטיול
   */
  getPackingListStream(tripId: number): Observable<string> {
    const url = `${this.baseUrl}/packingList/${tripId}`;
    
    // אנחנו משתמשים ב-createFetchStream כדי לקבל את הזרם חי
    return this.createFetchStream(url, null, 'GET').pipe(
      catchError(err => {
        console.warn('Streaming request failed, falling back to simple GET', err);
        // גיבוי למקרה שהסטרימינג נכשל - קריאה רגילה שתחזיר הכל בסוף
        return this.http.get(url, { responseType: 'text' });
      })
    );
  }

  getChatStream(message: string, conversationId: string): Observable<string> {
    const url = `${this.baseUrl}/chat`;
    const payload = { message, conversationId };

    return this.createFetchStream(url, payload, 'POST').pipe(
      catchError(err => {
        console.warn('Chat streaming failed, falling back to regular request', err);
        return this.http.post(url, payload, { responseType: 'text' });
      })
    );
  }

  /**
   * מתודה גנרית ליצירת זרם נתונים מול ה-Backend
   */
  private createFetchStream(url: string, body: any | null, method: 'GET' | 'POST'): Observable<string> {
    return new Observable<string>(observer => {
      const controller = new AbortController();
      const signal = controller.signal;
      
      const headers = new Headers();
      headers.append('Accept', 'text/event-stream');
      
      let fetchOptions: RequestInit = {
        method: method,
        headers: headers,
        signal: signal
      };
      
      if (body) {
        headers.append('Content-Type', 'application/json');
        fetchOptions.body = JSON.stringify(body);
      }

      fetch(url, fetchOptions)
      .then(async response => {
        if (!response.ok) {
          let txt: string;
          try {
            txt = await response.text();
          } catch {
            txt = response.statusText;
          }
          throw new Error(`HTTP ${response.status}: ${txt}`);
        }

        if (!response.body) {
          throw new Error('Response body is null');
        }
        
        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        
        // פונקציה לעיבוד כל חתיכת מידע (Chunk) שמגיעה
        const processChunk = ({ done, value }: { done: boolean, value: Uint8Array | undefined }): Promise<void> | void => {
          if (done) {
            observer.complete();
            return;
          }
          
          const chunk = decoder.decode(value, { stream: true });
          
          // פיצול לפי שורות - חשוב כי ה-AI שולח שורות טקסט
          const lines = chunk.split('\n');
          
          lines.forEach(line => {
            const trimmedLine = line.trim();
            if (trimmedLine) {
              // בדיקה אם השורה בפורמט SSE (מתחילה ב-data:)
              if (trimmedLine.startsWith('data:')) {
                // חיתוך ה-5 תווים הראשונים ("data:") ושליחת התוכן
                this.zone.run(() => observer.next(trimmedLine.substring(5).trim()));
              } else {
                // אם זה טקסט נקי (Flux רגיל), נשלח אותו כפי שהוא
                this.zone.run(() => observer.next(line));
              }
            }
          });
          
          // קריאה לחתיכה הבאה בזרם
          return reader.read().then(processChunk).catch(err => observer.error(err));
        };
        
        return reader.read().then(processChunk);
      })
      .catch(err => {
        if (!signal.aborted) {
          observer.error(err);
        }
      });

      // פונקציית הניקוי (Cleanup) - תתבצע ב-unsubscribe
      return () => {
        controller.abort();
      };
    });
  }
}
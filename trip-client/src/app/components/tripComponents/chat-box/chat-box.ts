import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService } from '../../../service/chat.service';

interface ChatLine {
  sender: 'user' | 'assistant' | 'system';
  text: string;
  isStreaming?: boolean;
}

@Component({
  selector: 'app-chat-box',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-box.html',
  styleUrls: ['./chat-box.css']
})
export class ChatBox {
  @Input() tripId!: number;

  chatLines: ChatLine[] = [];
  message = '';
  isSending = false;
  errorMessage: string | null = null;

  constructor(private chatService: ChatService) {}

  sendMessage(): void {
    if (!this.message.trim() || !this.tripId) {
      return;
    }

    const userMessage = this.message.trim();
    this.chatLines.push({ sender: 'user', text: userMessage });
    this.message = '';
    this.errorMessage = null;
    this.isSending = true;

    const assistantLine: ChatLine = { sender: 'assistant', text: '', isStreaming: true };
    this.chatLines.push(assistantLine);

    const conversationId = `trip-${this.tripId}`;

    this.chatService.getChatStream(userMessage, conversationId).subscribe({
      next: chunk => {
        assistantLine.text += chunk;
      },
      error: err => {
        console.error('Chat stream error', err);
        this.errorMessage = 'שגיאה בשיחה עם השרת. נסה שוב מאוחר יותר.';
        assistantLine.isStreaming = false;
        this.isSending = false;
      },
      complete: () => {
        assistantLine.isStreaming = false;
        this.isSending = false;
      }
    });
  }
}

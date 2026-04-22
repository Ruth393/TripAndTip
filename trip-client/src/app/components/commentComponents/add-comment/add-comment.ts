import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CommentService } from '../../../service/comment.service';
import { CommentToAdd, CommentDTO } from '../../../models/comment.model';


@Component({
selector: 'app-add-comment',
standalone: true,
imports: [CommonModule, FormsModule],
templateUrl: './add-comment.html',
styleUrls: ['./add-comment.css']
})
export class AddComment {
@Input() tripId!: number;
@Output() commentAdded = new EventEmitter<CommentDTO>();


newComment: string = '';
isSubmitting: boolean = false;


constructor(private commentService: CommentService) {}


submitComment() {
if (!this.newComment.trim() || this.isSubmitting) return;
this.isSubmitting = true;


const commentToAdd: CommentToAdd = {
comment: this.newComment.trim(),
date: new Date(),
trip: { id: this.tripId }
};


this.commentService.addComment(commentToAdd).subscribe({
next: (savedComment: CommentDTO) => {
this.newComment = '';
this.isSubmitting = false;
this.commentAdded.emit(savedComment);
},
error: (err) => {
console.error('שגיאה', err);
this.isSubmitting = false;
}
});
}
}
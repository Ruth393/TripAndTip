import { Component, Input, OnInit, OnChanges, SimpleChanges, ViewChild, ElementRef, ChangeDetectorRef, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CommentService } from '../../../service/comment.service';
import { CommentDTO } from '../../../models/comment.model';


@Component({
selector: 'app-list-comments',
standalone: true,
imports: [CommonModule],
templateUrl: './list-comments.html',
styleUrls: ['./list-comments.css']
})
export class ListComments implements OnInit, OnChanges {


@Input() id!: number;
@Output() commentsCountChange = new EventEmitter<number>();


comments: CommentDTO[] = [];
isLoading = true;


@ViewChild('commentsEnd') commentsEnd!: ElementRef;


constructor(
private commentService: CommentService,
private cdr: ChangeDetectorRef
) {}


ngOnInit(): void {
this.loadComments();
}


ngOnChanges(changes: SimpleChanges): void {
if (changes['id'] && this.id && !changes['id'].firstChange) {
this.loadComments();
}
}


loadComments() {
this.isLoading = true;
this.comments = [];
this.commentService.getCommentsByTripId(this.id).subscribe({
next: (comments) => {
this.comments = comments;
this.isLoading = false;
this.commentsCountChange.emit(comments.length);
this.cdr.detectChanges();
},
error: () => {
this.comments = [];
this.isLoading = false;
this.commentsCountChange.emit(0);
}
});
}

addComment(newComment: CommentDTO) {
    this.comments.push(newComment);
    this.commentsCountChange.emit(this.comments.length); 
    this.cdr.detectChanges();
    setTimeout(() => this.scrollToBottom(), 100);
  }


scrollToBottom(): void {
this.commentsEnd?.nativeElement?.scrollIntoView({ behavior: 'smooth' });
}


getUserAvatar(comment: CommentDTO): string {
const img = comment.user.image || comment.user.imagePath;
if (!img) return 'https://i.imgur.com/8Km9tLL.png';
if (img.startsWith('/9j/') || img.startsWith('iVBORw')) {
return 'data:image/jpeg;base64,' + img;
}
return `http://localhost:8080/images/${img}`;
}

}
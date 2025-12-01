import { Component ,OnInit} from '@angular/core';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  userName1: string = ''
  constructor(private userService: UserService) {}

  ngOnInit(): void {
    // בקשת מידע משתמש נוכחי מהשרת (ישתמש ב-Cookie HttpOnly)
    this.userService.getCurrentUser().subscribe({
      next: (res) => {
        if (res && res.name) this.userName1 = res.name;
      },
      error: () => {
        // אין משתמש מחובר
        this.userName1 = '';
      }
    });
  }
}
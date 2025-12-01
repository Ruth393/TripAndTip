import { Component ,OnInit} from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../material.module';
import {App} from '../app';
import { UserToSeeDTO } from '../models/user.model';
@Component({
  selector: 'app-header',
  standalone:true,
  imports: [RouterModule,MaterialModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
    getImageSrc(userToSeeDTO: UserToSeeDTO): string | null {
      const raw = (userToSeeDTO as any).image || (userToSeeDTO as any).imageUrl || (userToSeeDTO as any).imagePath;
      if (!raw || typeof raw !== 'string') return null;
  
      const trimmed = raw.trim();
      if (trimmed.startsWith('data:') || trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
        return trimmed;
      }
      return 'data:image/jpeg;base64,' + trimmed;
    }
}


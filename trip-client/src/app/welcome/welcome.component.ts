import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../material.module';

@Component({
  selector: 'app-welcome',
  standalone:true,
  imports: [RouterModule,MaterialModule],
  templateUrl: './welcome.component.html',
  styleUrl: './welcome.component.css'
})
export class WelcomeComponent {

}

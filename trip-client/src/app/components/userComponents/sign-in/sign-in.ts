import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SignIn }  from '../../../models/user.model';
import  {UserService}  from '../../../service/user.service';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from '../../../material.module';
@Component({
  selector: 'app-log-in',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule,MaterialModule],
  templateUrl: './sign-in.html',
  styleUrl: './sign-in.css'
})


export class SignInComponent {
public toSignIn:SignIn={
userName :"",
password :""
}
public notFound:boolean=false

constructor(private _userService:UserService,private router:Router){}
  getImageSrc(signIn: SignIn): string | null {
    const raw = (signIn as any).image || (signIn as any).imageUrl || (signIn as any).imagePath;
    if (!raw || typeof raw !== 'string') return null;

    const trimmed = raw.trim();
    if (trimmed.startsWith('data:') || trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
      return trimmed;
    }
    return 'data:image/jpeg;base64,' + trimmed;
  }
signIn(){
  this._userService.signIn(this.toSignIn).subscribe({
    next:(res)=>{
      console.log(res);
      console.log('sign-in response:', res);
      this.router.navigate(['/list-trips'])
    },
    error:(err)=>{
      this.notFound=true
      alert("not found");
      console.log(err);
    }
  })
}
}

import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SignUp } from '../../../models/user.model';
import { UserService } from '../../../service/user.service';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from '../../../material.module';
import { takeUntil } from 'rxjs/operators';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule,MaterialModule],
  templateUrl: './sign-up.html',
  styleUrl: './sign-up.css',
})


export class SignUpComponent {
   selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;
public toSignUp:SignUp={
  userName:"",
  email:"",
  password:""
}


public signUpsSuccess:boolean=false;
public alreadyExists:boolean=false;

constructor(private _userService:UserService,private router:Router, private sanitizer: DomSanitizer){}
  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0] || null;

    if (this.selectedFile) {
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result; // Base64 string
      };
      reader.readAsDataURL(this.selectedFile);
    } else {
      this.imagePreview = null;
    }
  }
  getImageSrc(signUp: SignUp): string | null {
    const raw = (signUp as any).image || (signUp as any).imageUrl || (signUp as any).imagePath;
    if (!raw || typeof raw !== 'string') return null;

    const trimmed = raw.trim();
    if (trimmed.startsWith('data:') || trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
      return trimmed;
    }
    return 'data:image/jpeg;base64,' + trimmed;
  }

    public getSafeImageUrl(base64Data: string): SafeUrl {
    if (!base64Data) {
      return this.sanitizer.bypassSecurityTrustUrl('assets/placeholder.png');
    }
    const dataUri = base64Data.startsWith('data:') 
      ? base64Data 
      : `data:image/jpeg;base64,${base64Data}`;
    return this.sanitizer.bypassSecurityTrustUrl(dataUri);
  }
signUp(){
  this._userService.signUp(this.toSignUp).subscribe({
    next:(res)=>{
      console.log(res);
      this.signUpsSuccess=true;
      console.log('sign-up response:', res);
      this.router.navigate(['/list-trips'])
    },
    error:(err)=>{
      console.log(err);
      if(err.status==409){
        this.alreadyExists=true;
        setTimeout(()=>{
          location.reload();
        },1500);
      }
    }
  })
}

}


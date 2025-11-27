import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SignUp } from '../../../models/user.model';
import { UserService } from '../../../service/user.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './sign-up.html',
  styleUrl: './sign-up.css',
})


export class SignUpComponent {

public toSignUp:SignUp={
  name:"",
  email:"",
  password:""
}


public signUpsSuccess:boolean=false;
public alreadyExists:boolean=false;

constructor(private _userService:UserService,private router:Router){}

signUp(){
  this._userService.signUp(this.toSignUp).subscribe({
    next:(res)=>{
      console.log(res);
      this.signUpsSuccess=true;
      localStorage.setItem('user',JSON.stringify(res))
      const savedUserStr=localStorage.getItem('user');
      if(savedUserStr){
      const savedUser=JSON.parse(savedUserStr);
      }
      else{
        console.error('Saving the localStorge failed')
      }
      this.router.navigate(['home'])
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


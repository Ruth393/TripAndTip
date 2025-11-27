import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SignIn }  from '../../../models/user.model';
import  {UserService}  from '../../../service/user.service';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-log-in',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './sign-in.html',
  styleUrl: './sign-in.css'
})


export class SignInComponent {
public toSignIn:SignIn={
name :"",
email :""
}
public notFound:boolean=false

constructor(private _userService:UserService,private router:Router){}

signIn(){
  this._userService.signIn(this.toSignIn).subscribe({
    next:(res)=>{
      console.log(res);
      localStorage.setItem('user',JSON.stringify(res))
      const savedUserStr=localStorage.getItem('user');
      if(savedUserStr){
      const savedUser=JSON.parse(savedUserStr);
      console.log('saving in localStorge:',savedUser);
      console.log(savedUser.id)
      }
      else{
        console.error('Saving the localStorge failed')
      }
      this.router.navigate(['home'])
    },
    error:(err)=>{
      this.notFound=true
      alert("not found");
      console.log(err);
    }
  })
}
}

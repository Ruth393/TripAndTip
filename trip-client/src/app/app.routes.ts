import { Routes } from '@angular/router';
import { TripDetails} from './components/tripComponents/trip-details/trip-details';
import { UploadTrip } from './components/tripComponents/upload-trip/upload-trip';
import { SignInComponent } from './components/userComponents/sign-in/sign-in';
import { SignUpComponent } from './components/userComponents/sign-up/sign-up';
import { UnFoundComponent } from './un-found/un-found.component';
import { TripListComponent } from './components/tripComponents/list-trips/list-trips';
import { HeaderComponent } from './header/header.component';
import { WelcomeComponent } from './welcome/welcome.component';
import { AddCategory } from './components/categoryComponents/add-category/add-category';
import { SignOutComponent } from './components/userComponents/sign-out/sign-out';
import { ListComments } from './components/commentComponents/list-comments/list-comments';


export const routes: Routes = [
    {path: "", redirectTo: "list-trips", pathMatch:'full'},
    {path: "upload-trip", component:UploadTrip},
    {path: "welcome", component: WelcomeComponent},
    {path: "header", component: HeaderComponent},
    {path: "sign-in", component: SignInComponent},
    {path: "sign-up", component: SignUpComponent},
    {path: "sign-out", component: SignOutComponent},
    {path: "list-trips", component:TripListComponent},
    {path: "list-components/:id", component:ListComments},
    {path: "trip-details/:id", component:TripDetails},
    {path: "add-category", component:AddCategory},
    {path: "**", component:UnFoundComponent}
];

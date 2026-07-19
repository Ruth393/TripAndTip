export default class Users{
id! :number
userName! :string
email! :string
password! :string
image?: string
imageUrl?: string
imagePath?: string
}
export class SignIn{
  // changed to email now that server expects credentials via email
  email! :string
  password! :string
  image?: string
  imageUrl?: string
  imagePath?: string
}
export class SignUp{
userName! :string
email! :string
password! :string
image?: string
imagePath?: string
imageUrl?: string
}

export interface AuthResponse {
token: string;
id?: number;
name?: string;
email?: string;
image?: string
imagePath?: string
imageUrl?: string
isAdmin?: boolean
roles?: string[]
authorities?: string[]
}
export interface UserToSeeDTO {
id?: number;
userName: string;
image?: string
imagePath?: string
imageUrl?: string
}


export default class Users{
id! :number
userName! :string
email! :string
password! :string
imageUrl?: string
imagePath?: string
}
export class SignIn{
userName! :string
password! :string
imageUrl?: string
imagePath?: string
}
export class SignUp{
userName! :string
email! :string
password! :string
imageUrl?: string
imagePath?: string
}

export interface AuthResponse {
token: string;
id?: number;
name?: string;
email?: string;
imageUrl?: string
imagePath?: string
}
export interface UserToSeeDTO {
token: string;
id?: number;
userName: string;
imageUrl?: string
imagePath?: string
}


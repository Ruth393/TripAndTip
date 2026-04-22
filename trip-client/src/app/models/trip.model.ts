import Category from "./category.model"
import Users,{UserToSeeDTO} from "./user.model"

export default class TripDTO{
id! :number
name! :string
description! :string
cost! :number
match! :string
user! :Users
category! :Category
imageUrl?: string
imagePath?: string
image?: string
}


export class TripToUpload
{
name! :string
description! :string
cost! :number
match! :string
users!: {id:number}
category! :{id: number}
imageUrl?: string
imagePath?: string
}
export  class TripListDTO{
id! :number
name! :string
description! :string
user! :UserToSeeDTO
category! :{id: number, name?: ""}
image?: string
imageUrl?: string
imagePath?: string
}
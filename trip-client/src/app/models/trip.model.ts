import Categorys from "./category.model"
import Users,{UserToSeeDTO} from "./user.model"

export default class TripDTO{
id! :number
name! :string
description! :string
cost! :number
match! :string
users! :Users
category! :Categorys
imageUrl?: string
imagePath?: string
}


export class TripToUpload
{
name! :string
description! :string
cost! :string
match! :string
users!: {id:number}
category! :{id: number, name?: ""}
imageUrl?: string
imagePath?: string
}
export  class TripListDTO{
id! :number
name! :string
description! :string
UserToSeeDTO! :UserToSeeDTO
image?: string
imageUrl?: string
imagePath?: string
}
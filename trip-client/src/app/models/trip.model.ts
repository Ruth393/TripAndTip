import Categorys from "./category.model"
import Users from "./user.model"

export default class Trip{
id! :number
name! :string
description! :string
cost! :number
match! :string
users! :Users
category! :Categorys
}


export class TripToUpload
{
name! :string
description! :string
cost! :string
match! :string
users!: {id:number}
category! :{id: number, name?: ""}
}

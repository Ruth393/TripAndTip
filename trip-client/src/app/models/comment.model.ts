import { UserToSeeDTO } from "./user.model";

export class CommentDTO {
  id!: number;
  comment!: string;
  date!: Date;
  user!: UserToSeeDTO;
  trip!: number; 
}

export class CommentToAdd {
  comment!: string;
  date!: Date;
  trip!: {"id": number};
}

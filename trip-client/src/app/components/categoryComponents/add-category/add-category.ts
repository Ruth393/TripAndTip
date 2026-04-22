import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoryService } from '../../../service/category.service';
import Category from '../../../models/category.model';

@Component({
  selector: 'app-add-category',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-category.html',
  styleUrl: './add-category.css',
})
export class AddCategory {
  public model: Partial<Category> = { category: '' };
  public success = false;
  public error: string | null = null;

  constructor(private _categoryService: CategoryService) {}

  addCategory() {
    this.error = null;
    if (!this.model.category || this.model.category.trim().length === 0) {
      this.error = 'Category name is required';
      return;
    }
    this._categoryService.addCategory(this.model).subscribe({
      next: (res) => {
        this.success = true;
        this.model = { category: '' };
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to add category';
      }
    });
  }
}

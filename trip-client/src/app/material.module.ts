// src/app/material.module.ts

import { NgModule } from '@angular/core';

// ייבוא מודולי Material
// --- (רכיבי בסיס וניווט) ---
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav'; 

// --- (רכיבי טפסים נפוצים, מומלץ לטפסים) ---
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

const MATERIAL_COMPONENTS = [
  // בסיס וקונטיינרים
  MatButtonModule,
  MatCardModule,
  MatIconModule,
  
  // ניווט ולייאוט
  MatToolbarModule,
  MatSidenavModule,
  
  // טפסים
  MatFormFieldModule,
  MatInputModule,
  MatSelectModule,
  MatProgressSpinnerModule
];

@NgModule({
  // מייבא את המודולים לתוך ה-NgModule
  imports: [...MATERIAL_COMPONENTS],
  // מייצא את המודולים החוצה כדי שיהיו זמינים לרכיבים המשתמשים ב-MaterialModule
  exports: [...MATERIAL_COMPONENTS] 
})
export class MaterialModule { }
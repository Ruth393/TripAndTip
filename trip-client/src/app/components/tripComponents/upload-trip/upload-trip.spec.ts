import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';

import { UploadTrip } from './upload-trip';
import { TripService } from '../../../service/trip.service';
import { CategoryService } from '../../../service/category.service';
import { UserService } from '../../../service/user.service';

describe('UploadTrip', () => {
  let component: UploadTrip;
  let fixture: ComponentFixture<UploadTrip>;
  let tripService: jasmine.SpyObj<TripService>;

  beforeEach(async () => {
    tripService = jasmine.createSpyObj('TripService', ['uploadTrip']);
    tripService.uploadTrip.and.returnValue(of({} as any));

    const categoryService = jasmine.createSpyObj('CategoryService', ['getCategoriesFromServer']);
    categoryService.getCategoriesFromServer.and.returnValue(of([]));

    const userService = jasmine.createSpyObj('UserService', ['getCurrentUser']);
    userService.getCurrentUser.and.returnValue(of({ id: 42, token: 'token' } as any));

    await TestBed.configureTestingModule({
      imports: [UploadTrip],
      providers: [
        { provide: TripService, useValue: tripService },
        { provide: CategoryService, useValue: categoryService },
        { provide: UserService, useValue: userService },
        { provide: Router, useValue: jasmine.createSpyObj('Router', ['navigate']) },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UploadTrip);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should include the logged-in user id in the uploaded trip payload', async () => {
    component.selectedFile = new File(['image'], 'trip.png', { type: 'image/png' });
    component.newTrip.name = 'Trip to test';
    component.newTrip.category = { id: 7 } as any;
    component.currentUser = { id: 42 } as any;
    component.isUserLoggedIn = true;

    component.uploadTrip();

    const formData = tripService.uploadTrip.calls.mostRecent().args[0] as FormData;
    const tripBlob = formData.get('trip') as Blob;
    const payload = JSON.parse(await tripBlob.text());

    expect(payload.user.id).toBe(42);
    expect(payload.users.id).toBe(42);
  });
});

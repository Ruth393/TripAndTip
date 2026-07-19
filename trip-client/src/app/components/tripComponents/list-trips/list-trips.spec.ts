import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TripListComponent } from './list-trips';

describe('ListTrips', () => {
  let component: TripListComponent;
  let fixture: ComponentFixture<TripListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TripListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update avatar for all trips owned by the updated user', () => {
    component.tripList = [
      { id: 1, name: 'Trip 1', description: '', user: { id: 7, userName: 'User 1', image: 'old-image' }, category: { id: 1 } } as any,
      { id: 2, name: 'Trip 2', description: '', user: { id: 8, userName: 'User 2', image: 'other-image' }, category: { id: 1 } } as any
    ];

    component.updateTripsUserImage({ id: 7, image: 'new-image', imagePath: 'new.png', imageUrl: 'https://example.com/new.png' });

    expect(component.tripList[0].user.image).toBe('new-image');
    expect(component.tripList[0].user.imagePath).toBe('new.png');
    expect(component.tripList[0].user.imageUrl).toBe('https://example.com/new.png');
    expect(component.tripList[1].user.image).toBe('other-image');
  });
});

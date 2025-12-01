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
});

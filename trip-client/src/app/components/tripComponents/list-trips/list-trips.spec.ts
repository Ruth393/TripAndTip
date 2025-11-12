import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListTrips } from './list-trips';

describe('ListTrips', () => {
  let component: ListTrips;
  let fixture: ComponentFixture<ListTrips>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListTrips]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListTrips);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

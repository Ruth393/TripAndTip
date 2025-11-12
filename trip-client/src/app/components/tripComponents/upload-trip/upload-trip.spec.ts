import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadTrip } from './upload-trip';

describe('UploadTrip', () => {
  let component: UploadTrip;
  let fixture: ComponentFixture<UploadTrip>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadTrip]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UploadTrip);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

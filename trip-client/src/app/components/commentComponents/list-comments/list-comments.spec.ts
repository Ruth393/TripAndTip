import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListComments } from './list-comments';

describe('ListComments', () => {
  let component: ListComments;
  let fixture: ComponentFixture<ListComments>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListComments]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListComments);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

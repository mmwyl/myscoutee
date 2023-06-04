import { CommonModule, DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UIModule } from '../ui/ui.module';
import {
  BasketComponent,
  CarFormComponent,
  EditorComponent,
  EventComponent,
  EventFilterComponent,
  PromotionFilterComponent,
  GroupFilterComponent,
  EventFormComponent,
  FeedbackFormComponent,
  PanelComponent,
  ProfileComponent,
  QrcodeComponent,
  SchoolComponent,
  SettingComponent,
  HelpComponent,
  GroupFormComponent,
  RewardComponent,
} from './components';
import { JobRoutingModule } from './job-routing.module';
@NgModule({
  declarations: [
    CarFormComponent,
    ProfileComponent,
    SettingComponent,
    EventFormComponent,
    EventFilterComponent,
    PromotionFilterComponent,
    GroupFilterComponent,
    PanelComponent,
    EditorComponent,
    SchoolComponent,
    BasketComponent,
    EventComponent,
    FeedbackFormComponent,
    QrcodeComponent,
    HelpComponent,
    GroupFormComponent,
    RewardComponent,
  ],
  imports: [
    UIModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    JobRoutingModule,
  ],
  providers: [DatePipe],
})
export class JobModule { }

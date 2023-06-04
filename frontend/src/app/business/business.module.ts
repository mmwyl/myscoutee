import { CommonModule, DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UIModule } from '../ui/ui.module';
import { BusinessRoutingModule } from './business-routing.module';
import {
  BasketComponent, CampaignFormComponent, CarFormComponent,
  EditorComponent,
  EventComponent,
  EventFilterComponent, EventFormComponent,
  FeedbackFormComponent, GroupFilterComponent, GroupFormComponent, HelpComponent, PanelComponent,
  ProfileComponent, ProfileStatusComponent, QrcodeComponent, RewardComponent, SettingComponent
} from './components';
@NgModule({
  declarations: [
    CarFormComponent,
    ProfileComponent,
    SettingComponent,
    EventFormComponent,
    EventFilterComponent,
    GroupFilterComponent,
    PanelComponent,
    EditorComponent,
    BasketComponent,
    EventComponent,
    FeedbackFormComponent,
    QrcodeComponent,
    HelpComponent,
    GroupFormComponent,
    RewardComponent,
    ProfileStatusComponent,
    CampaignFormComponent,
  ],
  imports: [
    UIModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    BusinessRoutingModule,
  ],
  providers: [DatePipe],
})
export class BusinessModule {}

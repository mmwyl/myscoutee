import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import {
  MsEditor,
  MsList,
  MsPanel,
  MsQrcode,
  MsAction,
  MsCalendar,
  MsDialog,
} from '../../lib';
import { MaterialModule } from './material.module';

const msComponents = [
  MsEditor,
  MsList,
  MsPanel,
  MsQrcode,
  MsAction,
  MsCalendar,
  MsDialog,
  // MsContainer,
  // https://stackoverflow.com/questions/39112891/angular-2-rc5-router-outlet-inside-another-router-outlet
];
@NgModule({
  declarations: [...msComponents],
  imports: [CommonModule, MaterialModule],
  exports: [...msComponents, MaterialModule],
})
export class UIModule {}

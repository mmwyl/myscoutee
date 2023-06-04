import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MsList } from 'src/lib';
import {
  BasketComponent,
  CampaignFormComponent,
  CarFormComponent,
  EventFormComponent,
  FeedbackFormComponent,
  GroupFormComponent,
  PanelComponent,
  ProfileComponent,
  ProfileStatusComponent,
  QrcodeComponent,
  RewardComponent,
  SettingComponent,
} from './components';

// filter, add, attach
const routes: Routes = [
  { path: '', redirectTo: 'activity', pathMatch: 'full' },
  {
    path: 'activity',
    data: {
      reuse: true,
      icon: 'notifications',
    },
    children: [
      { path: '', redirectTo: 'events', pathMatch: 'full' },
      {
        path: 'events',
        component: MsList,
        data: {
          reuse: true,
          icon: 'insert_invitation',
          actions: [
            {
              // component: EventFormComponent,
              component: QrcodeComponent,
              type: 'verify',
            },
            {
              // component: EventFormComponent,
              component: QrcodeComponent,
              type: 'code',
            },
          ],
          multiple: true,
          event: true, //temporary
          animation: 'One',
          group: 'date',
        },
      },
      {
        path: 'events/:id',
        data: {
          icon: 'insert_invitation',
          dialog: true,
        },
        children: [
          { path: '', redirectTo: 'items', pathMatch: 'full' },
          {
            path: 'items',
            component: MsList,
            data: {
              reuse: true,
              icon: 'list',
              actions: [
                {
                  component: EventFormComponent,
                  type: 'add',
                  icon: 'add',
                },
                {
                  component: EventFormComponent,
                  type: 'edit',
                },
              ],
              event: true, //temporary
              animation: 'One1',
            },
          },
          {
            path: 'items/:id',
            data: {
              icon: 'insert_invitation',
              dialog: true,
            },
            children: [
              { path: '', redirectTo: 'members', pathMatch: 'full' },
              {
                path: 'members',
                component: MsList,
                data: {
                  reuse: true,
                  animation: 'Two1',
                },
              },
            ],
          },
          {
            path: 'members',
            component: MsList,
            data: {
              animation: 'One',
              reuse: true,
              icon: 'groups',
              actions: [
                {
                  component: BasketComponent,
                  type: 'add',
                  icon: 'add',
                  multiple: true,
                  // only profiles - members url part will be replaced by profiles
                  url: '/games/rate_met', // backend call - not rate_met, filter out already added members
                },
              ],
            },
            children: [
              { path: '', redirectTo: 'schools', pathMatch: 'full' },
              {
                path: 'schools',
                component: MsList,
                data: {
                  reuse: true,
                  animation: 'One1',
                },
              },
            ],
          },
          {
            path: 'feedbacks',
            component: MsList,
            data: {
              reuse: true,
              icon: 'feedback_ext',
              actions: [
                {
                  component: FeedbackFormComponent,
                  type: 'add',
                  icon: 'add',
                },
                {
                  component: FeedbackFormComponent,
                  type: 'edit',
                },
              ],
            },
          },
        ],
      },
      {
        path: 'invitations',
        component: MsList,
        data: {
          reuse: true,
          icon: 'mail',
          event: true, //temporary
          animation: 'Two',
        },
      },
      {
        path: 'invitations/:id',
        data: {
          icon: 'insert_invitation',
          dialog: true,
        },
        children: [
          { path: '', redirectTo: 'items', pathMatch: 'full' },
          {
            path: 'items',
            component: MsList,
            data: {
              reuse: true,
              icon: 'list',
              event: true, //temporary
              animation: 'Two1',
            },
          },

          {
            path: 'members',
            component: MsList,
            data: {
              reuse: true,
              icon: 'groups',
              actions: [],
            },
          },
          {
            path: 'feedbacks',
            component: MsList,
            data: {
              reuse: true,
              icon: 'feedback_ext',
            },
          },
        ],
      },
      {
        path: 'promotions',
        component: MsList,
        data: {
          reuse: true,
          icon: 'campaign',
          animation: 'One',
        },
      },
      {
        path: 'promotions/:id',
        data: {
          icon: 'insert_invitation',
          dialog: true,
        },
        children: [
          { path: '', redirectTo: 'items', pathMatch: 'full' },
          {
            path: 'items',
            component: MsList,
            data: {
              reuse: true,
              event: true, //temporary
              animation: 'One1',
            },
          },
        ],
      },
    ],
  },
  {
    path: 'user',
    data: {
      reuse: true,
      icon: 'person',
      //type: 'P',
    },
    children: [
      { path: '', redirectTo: 'profile', pathMatch: 'full' },
      {
        path: 'profile',
        component: PanelComponent,
        data: {
          reuse: true,
          icon: 'profile_ext',
          actions: [
            {
              component: RewardComponent,
              type: 'share',
              icon: 'send',
            },
            {
              component: RewardComponent,
              type: 'reward',
              icon: 'military_tech',
            },
            {
              component: SettingComponent,
              type: 'logout',
              icon: 'logout',
              color: 'warn',
            },
            { component: ProfileComponent, type: 'edit', icon: 'edit' },
          ],
        },
      },
      {
        path: 'cars',
        component: MsList,
        data: {
          reuse: true,
          icon: 'airport_shuttle',
          actions: [
            {
              component: CarFormComponent,
              type: 'add',
              icon: 'add',
            },
            {
              component: CarFormComponent,
              type: 'edit',
            },
          ],
          event: true, //temporary
        },
      },
      // with registered profession, offer service az ismerosoknek - festes, villanyszereles stb.??
      // itt fontos, hogy a munka elvegzeseert jar a csillag nem a kulsoert

      // job search (recommendation menu - jobs / events / people),
      // job announcement (profile, create campaign - job advertisement or idea - last two icon),
      // game menu (click on each other)...

      // it's for group invite, when you are going to restructure a team (at the top menu bar)
      // you can select event created by organizer or you can organize your own by group
      // admin groups has statistics tab - who is the less popular, why? who is the most populat why?
      // why is when you like by category for people
      // show optimal groups (the size of the group is configurable)
      {
        path: 'groups',
        component: MsList,
        data: {
          reuse: true,
          icon: 'groups',
          actions: [
            {
              component: GroupFormComponent,
              type: 'add',
              icon: 'group_add',
            },
            {
              component: GroupFormComponent,
              type: 'edit',
            },
            {
              component: GroupFormComponent,
              type: 'share',
            },
          ],
        },
      },
      {
        path: 'groups/:id',
        data: {
          icon: 'insert_invitation',
          dialog: true,
        },
        children: [
          { path: '', redirectTo: 'profiles', pathMatch: 'full' },
          {
            path: 'profiles',
            component: MsList,
            data: {
              icon: 'groups',
              reuse: true,
              // icon: 'list',
              actions: [
                {
                  component: ProfileStatusComponent,
                  type: 'edit',
                },
              ],
            },
          },
          {
            path: 'events',
            component: MsList,
            data: {
              reuse: true,
              icon: 'local_activity',
              actions: [
                {
                  // component: EventFormComponent,
                  component: QrcodeComponent,
                  type: 'verify',
                },
                {
                  // component: EventFormComponent,
                  component: QrcodeComponent,
                  type: 'code',
                },
              ],
              multiple: true,
              event: true, //temporary
              animation: 'One',
            },
          },
          {
            path: 'events/:id',
            data: {
              icon: 'local_activity',
              dialog: true,
            },
            children: [
              { path: '', redirectTo: 'items', pathMatch: 'full' },
              {
                path: 'items',
                component: MsList,
                data: {
                  reuse: true,
                  actions: [
                    {
                      component: EventFormComponent,
                      type: 'add',
                      icon: 'add',
                    },
                    {
                      component: EventFormComponent,
                      type: 'edit',
                    },
                  ],
                  event: true, //temporary
                  animation: 'One1',
                },
              },
            ],
          },
        ],
      },
    ],
  },
  {
    path: 'promotions',
    component: MsList,
    data: {
      reuse: true,
      icon: 'campaign',
      actions: [
        {
          component: CampaignFormComponent,
          type: 'add',
          icon: 'add',
        },
        {
          component: CampaignFormComponent,
          type: 'edit',
        },
      ],
    },
    children: [
      {
        path: 'templates/:id',
        data: {
          icon: 'insert_invitation',
          dialog: true,
        },
        children: [
          { path: '', redirectTo: 'items', pathMatch: 'full' },
          {
            path: 'items',
            component: MsList,
            data: {
              actions: [
                {
                  component: EventFormComponent,
                  type: 'add',
                  icon: 'add',
                },
                {
                  component: EventFormComponent,
                  type: 'edit',
                },
              ],
              event: true, //temporary
            },
          },
        ],
      },
    ],
  },
  {
    path: 'promotions/:id',
    data: {
      icon: 'insert_invitation',
      dialog: true,
    },
    children: [
      { path: '', redirectTo: 'events', pathMatch: 'full' },
      {
        path: 'events',
        component: MsList,
        data: {
          reuse: true,
          icon: 'list',
          event: true, //temporary
          animation: 'One1',
          actions: [
            {
              component: EventFormComponent,
              type: 'edit',
            },
          ],
        },
      },
      {
        path: 'events/:id',
        data: {
          icon: 'insert_invitation',
          dialog: true,
        },
        children: [
          { path: '', redirectTo: 'items', pathMatch: 'full' },
          {
            path: 'items',
            component: MsList,
            data: {
              reuse: true,
              icon: 'list',
              actions: [
                {
                  component: EventFormComponent,
                  type: 'add',
                  icon: 'add',
                },
                {
                  component: EventFormComponent,
                  type: 'edit',
                },
              ],
              event: true, //temporary
              animation: 'One1',
            },
          },
          {
            path: 'members',
            component: MsList,
            data: {
              reuse: true,
              icon: 'groups',
              animation: 'One',
              actions: [
                {
                  component: BasketComponent,
                  type: 'add',
                  icon: 'add',
                  multiple: true,
                  // only profiles - members url part will be replaced by profiles
                  url: '/games/rate_met', // backend call - not rate_met, filter out already added members
                },
              ],
            },
            children: [
              { path: '', redirectTo: 'schools', pathMatch: 'full' },
              {
                path: 'schools',
                component: MsList,
                data: {
                  reuse: true,
                  animation: 'One1',
                },
              },
            ],
          },
          {
            path: 'feedbacks',
            component: MsList,
            data: {
              reuse: true,
              icon: 'feedback_ext',
              actions: [
                {
                  component: FeedbackFormComponent,
                  type: 'add',
                  icon: 'add',
                },
                {
                  component: FeedbackFormComponent,
                  type: 'edit',
                },
              ],
            },
          },
        ],
      },
      {
        path: 'members',
        component: MsList,
        data: {
          animation: 'One',
          reuse: true,
          icon: 'groups',
        },
        children: [
          { path: '', redirectTo: 'schools', pathMatch: 'full' },
          {
            path: 'schools',
            component: MsList,
            data: {
              reuse: true,
              animation: 'One1',
            },
          },
        ],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BusinessRoutingModule {}

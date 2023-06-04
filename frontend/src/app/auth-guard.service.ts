import { Injectable } from '@angular/core';
import { CanLoad, Route, Router, UrlSegment, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { NavigationService } from './navigation.service';
import { HttpService } from './services/http.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuardService implements CanLoad {
  constructor(
    public navService: NavigationService,
    public httpService: HttpService,
    public router: Router
  ) {}
  canLoad(
    route: Route,
    segments: UrlSegment[]
  ):
    | boolean
    | UrlTree
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree> {
    return new Observable<boolean>((observer) => {
      if (this.navService.token === undefined) {
        observer.next(false);
        observer.complete();
      } else {
        observer.next(true);
        observer.complete();
      }
    });
  }
}

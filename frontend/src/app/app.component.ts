import { Component, OnInit } from '@angular/core';
import { WebSocketService } from './services/websocket.service';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'Fire Captain System';
  isAuthenticated = false;

  constructor(
    private webSocketService: WebSocketService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.authService.isAuthenticated$.subscribe(
      isAuth => this.isAuthenticated = isAuth
    );
    
    if (this.isAuthenticated) {
      this.webSocketService.connect();
    }
  }
}

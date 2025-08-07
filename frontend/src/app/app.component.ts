import { Component, OnInit } from '@angular/core';
import { WebSocketService } from './services/websocket.service';
import { AuthService } from './services/auth.service';

/**
 * 消防司令システムのメインアプリケーションコンポーネント
 * 
 * アプリケーションのルートコンポーネントとして機能し、
 * 認証状態の管理とWebSocket接続の制御を行います。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  /** アプリケーションのタイトル */
  title = 'Fire Captain System';
  
  /** ユーザーの認証状態 */
  isAuthenticated = false;

  /**
   * コンストラクタ
   * 
   * @param webSocketService WebSocket通信サービス
   * @param authService 認証サービス
   */
  constructor(
    private webSocketService: WebSocketService,
    private authService: AuthService
  ) {}

  /**
   * コンポーネント初期化時の処理
   * 
   * 認証状態の監視とWebSocket接続の管理を行います。
   */
  ngOnInit() {
    // 認証状態の変更を監視
    this.authService.isAuthenticated$.subscribe(
      isAuth => this.isAuthenticated = isAuth
    );
    
    // 認証済みの場合、WebSocket接続を開始
    if (this.isAuthenticated) {
      this.webSocketService.connect();
    }
  }
}

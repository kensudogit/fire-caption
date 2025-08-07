import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';

/**
 * WebSocketメッセージのインターフェース
 * 
 * サーバーとのリアルタイム通信で使用するメッセージの構造を定義します。
 */
export interface WebSocketMessage {
  type: string;    // メッセージの種類
  data: any;       // メッセージのデータ
}

/**
 * WebSocket通信サービス
 * 
 * 消防司令システムのリアルタイム通信を担当します。
 * 緊急通報の即座通知、ダッシュボードの自動更新、
 * ステータス変更のリアルタイム反映などの機能を提供します。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  /** WebSocket接続の管理 */
  private socket$: WebSocketSubject<WebSocketMessage> | null = null;
  
  /** 緊急通報用のSubject */
  private emergencyCallSubject = new Subject<any>();
  
  /** ダッシュボード更新用のSubject */
  private dashboardUpdateSubject = new Subject<any>();
  
  /** ステータス更新用のSubject */
  private statusUpdateSubject = new Subject<any>();
  
  /** システムアラート用のSubject */
  private systemAlertSubject = new Subject<any>();

  /** WebSocket接続URL */
  private readonly wsUrl = 'ws://localhost:8080/ws';

  /**
   * WebSocket接続を開始
   * 
   * サーバーとのリアルタイム通信を確立し、
   * メッセージの受信処理を開始します。
   */
  connect(): void {
    if (!this.socket$ || this.socket$.closed) {
      this.socket$ = webSocket<WebSocketMessage>(this.wsUrl);
      
      this.socket$.subscribe({
        next: (message) => this.handleMessage(message),
        error: (error) => console.error('WebSocket error:', error),
        complete: () => console.log('WebSocket connection closed')
      });
    }
  }

  /**
   * WebSocket接続を切断
   * 
   * サーバーとのリアルタイム通信を終了します。
   */
  disconnect(): void {
    if (this.socket$) {
      this.socket$.complete();
      this.socket$ = null;
    }
  }

  /**
   * 受信メッセージの処理
   * 
   * サーバーから受信したメッセージの種類に応じて
   * 適切なSubjectにデータを送信します。
   * 
   * @param message 受信したメッセージ
   */
  private handleMessage(message: WebSocketMessage): void {
    switch (message.type) {
      case 'EMERGENCY_CALL':      // 緊急通報
        this.emergencyCallSubject.next(message.data);
        break;
      case 'DASHBOARD_UPDATE':    // ダッシュボード更新
        this.dashboardUpdateSubject.next(message.data);
        break;
      case 'STATUS_UPDATE':       // ステータス更新
        this.statusUpdateSubject.next(message.data);
        break;
      case 'SYSTEM_ALERT':        // システムアラート
        this.systemAlertSubject.next(message.data);
        break;
      default:
        console.log('Unknown message type:', message.type);
    }
  }

  /**
   * 緊急通報のObservableストリームを取得
   * 
   * @returns 緊急通報のObservable
   */
  onEmergencyCall(): Observable<any> {
    return this.emergencyCallSubject.asObservable();
  }

  /**
   * ダッシュボード更新のObservableストリームを取得
   * 
   * @returns ダッシュボード更新のObservable
   */
  onDashboardUpdate(): Observable<any> {
    return this.dashboardUpdateSubject.asObservable();
  }

  /**
   * ステータス更新のObservableストリームを取得
   * 
   * @returns ステータス更新のObservable
   */
  onStatusUpdate(): Observable<any> {
    return this.statusUpdateSubject.asObservable();
  }

  /**
   * システムアラートのObservableストリームを取得
   * 
   * @returns システムアラートのObservable
   */
  onSystemAlert(): Observable<any> {
    return this.systemAlertSubject.asObservable();
  }

  /**
   * サーバーにメッセージを送信
   * 
   * @param message 送信するメッセージ
   */
  sendMessage(message: WebSocketMessage): void {
    if (this.socket$ && !this.socket$.closed) {
      this.socket$.next(message);
    }
  }

  /**
   * 特定のトピックにサブスクライブ
   * 
   * @param topic サブスクライブするトピック名
   */
  subscribeToTopic(topic: string): void {
    this.sendMessage({
      type: 'SUBSCRIBE',
      data: { topic }
    });
  }

  /**
   * 特定のトピックからアンサブスクライブ
   * 
   * @param topic アンサブスクライブするトピック名
   */
  unsubscribeFromTopic(topic: string): void {
    this.sendMessage({
      type: 'UNSUBSCRIBE',
      data: { topic }
    });
  }

  /**
   * ステータス更新を送信
   * 
   * @param callId 通報ID
   * @param status 新しいステータス
   */
  sendStatusUpdate(callId: number, status: string): void {
    this.sendMessage({
      type: 'STATUS_UPDATE',
      data: { callId, status }
    });
  }

  // Send location updates
  sendLocationUpdate(callId: number, latitude: number, longitude: number): void {
    this.sendMessage({
      type: 'LOCATION_UPDATE',
      data: { callId, latitude, longitude }
    });
  }

  // Check connection status
  isConnected(): boolean {
    return this.socket$ !== null && !this.socket$.closed;
  }
}

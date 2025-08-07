import { Component, OnInit, OnDestroy } from '@angular/core';
import { EmergencyCallService } from '../../services/emergency-call.service';
import { WebSocketService } from '../../services/websocket.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

/**
 * ダッシュボードコンポーネント
 * 
 * 消防司令システムのメインダッシュボードを提供します。
 * 緊急通報の統計情報、最近の通報、リアルタイム更新などの
 * 機能を含みます。WebSocketを使用してリアルタイムで
 * データを更新します。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  /** コンポーネントの破棄を管理するSubject */
  private destroy$ = new Subject<void>();
  
  // ダッシュボードデータ
  /** 総通報数 */
  totalCalls = 0;
  /** アクティブな通報数（出動中・現場到着） */
  activeCalls = 0;
  /** 待機中の通報数 */
  pendingCalls = 0;
  /** 完了した通報数 */
  completedCalls = 0;
  
  // 最近の通報
  /** 最近の緊急通報リスト（最大5件） */
  recentCalls: any[] = [];
  
  // ローディング状態
  /** データ読み込み中のフラグ */
  loading = true;
  
  // チャートデータ
  /** 通報ステータス別の円グラフデータ */
  chartData: any = {
    labels: ['Pending', 'Dispatched', 'On Scene', 'Cleared'],
    datasets: [{
      data: [0, 0, 0, 0],
      backgroundColor: ['#ff9800', '#2196f3', '#f44336', '#4caf50']
    }]
  };

  /**
   * コンストラクタ
   * 
   * @param emergencyCallService 緊急通報管理サービス
   * @param webSocketService WebSocket通信サービス
   */
  constructor(
    private emergencyCallService: EmergencyCallService,
    private webSocketService: WebSocketService
  ) {}

  /**
   * コンポーネント初期化時の処理
   * 
   * ダッシュボードデータの読み込みとWebSocketリスナーの設定を行います。
   */
  ngOnInit() {
    this.loadDashboardData();
    this.setupWebSocketListeners();
  }

  /**
   * コンポーネント破棄時の処理
   * 
   * メモリリークを防ぐため、すべてのサブスクリプションを解除します。
   */
  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * ダッシュボードデータの読み込み
   * 
   * 通報サマリーと最近の通報データをAPIから取得し、
   * チャートデータを更新します。
   */
  private loadDashboardData() {
    this.loading = true;
    
    // 通報サマリーの読み込み
    this.emergencyCallService.getCallSummary()
      .pipe(takeUntil(this.destroy$))
      .subscribe(summary => {
        this.pendingCalls = summary.pendingCount;
        this.activeCalls = summary.dispatchedCount + summary.onSceneCount;
        this.completedCalls = summary.clearedCount;
        this.totalCalls = this.pendingCalls + this.activeCalls + this.completedCalls;
        
        // チャートデータの更新
        this.chartData.datasets[0].data = [
          summary.pendingCount,
          summary.dispatchedCount,
          summary.onSceneCount,
          summary.clearedCount
        ];
        
        this.loading = false;
      });

    // 最近の通報の読み込み
    this.emergencyCallService.getActiveCalls()
      .pipe(takeUntil(this.destroy$))
      .subscribe(calls => {
        this.recentCalls = calls.slice(0, 5); // 最新5件を取得
      });
  }

  /**
   * WebSocketリスナーの設定
   * 
   * リアルタイムでダッシュボードを更新するための
   * WebSocketリスナーを設定します。
   */
  private setupWebSocketListeners() {
    // 新しい緊急通報のリスナー
    this.webSocketService.onEmergencyCall()
      .pipe(takeUntil(this.destroy$))
      .subscribe(call => {
        this.loadDashboardData(); // 新しい通報が来たらデータを更新
      });

    // ダッシュボード更新のリスナー
    this.webSocketService.onDashboardUpdate()
      .pipe(takeUntil(this.destroy$))
      .subscribe(update => {
        this.loadDashboardData(); // ダッシュボード更新通知があったらデータを更新
      });
  }

  /**
   * ステータスに応じた色を取得
   * 
   * @param status 通報ステータス
   * @returns ステータスに対応する色コード
   */
  getStatusColor(status: string): string {
    switch (status) {
      case 'PENDING': return '#ff9800';    // オレンジ（待機中）
      case 'DISPATCHED': return '#2196f3'; // 青（出動指示済み）
      case 'EN_ROUTE': return '#2196f3';   // 青（出動中）
      case 'ON_SCENE': return '#f44336';
      case 'CLEARED': return '#4caf50';
      default: return '#757575';
    }
  }

  /**
   * 優先度に応じた色を取得
   * 
   * @param priority 優先度
   * @returns 優先度に対応する色コード
   */
  getPriorityColor(priority: string): string {
    switch (priority) {
      case 'CRITICAL': return '#f44336';
      case 'HIGH': return '#ff9800';
      case 'MEDIUM': return '#2196f3';
      case 'LOW': return '#4caf50';
      default: return '#757575';
    }
  }
}

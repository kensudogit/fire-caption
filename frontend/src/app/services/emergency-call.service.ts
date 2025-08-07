import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

/**
 * 緊急通報情報のインターフェース
 * 
 * フロントエンドで使用する緊急通報データの構造を定義します。
 */
export interface EmergencyCall {
  id?: number;                    // 通報ID
  callNumber: string;             // 通報番号
  callerName?: string;            // 通報者名
  callerPhone?: string;           // 通報者電話番号
  incidentAddress: string;        // 事故事象発生場所
  latitude?: number;              // 緯度
  longitude?: number;             // 経度
  incidentDescription?: string;   // 事故事象の詳細説明
  incidentType: IncidentType;     // 事故事象の種類
  priorityLevel: PriorityLevel;   // 優先度レベル
  status: CallStatus;             // 通報ステータス
  receivedAt?: string;            // 通報受信時刻
  dispatchedAt?: string;          // 出動指示時刻
  arrivedAt?: string;             // 現場到着時刻
  clearedAt?: string;             // 現場撤収時刻
  assignedStation?: any;          // 担当消防署
  assignedFirefighters?: any[];   // 担当消防士
}

/**
 * 事故事象の種類を定義する列挙型
 */
export enum IncidentType {
  FIRE = 'FIRE',                     // 火災
  MEDICAL_EMERGENCY = 'MEDICAL_EMERGENCY', // 医療緊急事態
  TRAFFIC_ACCIDENT = 'TRAFFIC_ACCIDENT',   // 交通事故
  HAZMAT = 'HAZMAT',                 // 危険物事故
  RESCUE = 'RESCUE',                 // 救助
  FALSE_ALARM = 'FALSE_ALARM',       // 誤報
  OTHER = 'OTHER'                    // その他
}

/**
 * 優先度レベルを定義する列挙型
 */
export enum PriorityLevel {
  LOW = 'LOW',       // 低優先度
  MEDIUM = 'MEDIUM', // 中優先度
  HIGH = 'HIGH',     // 高優先度
  CRITICAL = 'CRITICAL' // 緊急優先度
}

/**
 * 通報ステータスを定義する列挙型
 */
export enum CallStatus {
  PENDING = 'PENDING',     // 待機中
  DISPATCHED = 'DISPATCHED', // 出動指示済み
  EN_ROUTE = 'EN_ROUTE',   // 出動中
  ON_SCENE = 'ON_SCENE',   // 現場到着
  CLEARED = 'CLEARED',     // 現場撤収
  CANCELLED = 'CANCELLED'  // キャンセル
}

/**
 * 通報サマリー情報のインターフェース
 * 
 * ダッシュボードで表示する通報統計情報を定義します。
 */
export interface CallSummary {
  pendingCount: number;    // 待機中の通報数
  dispatchedCount: number; // 出動指示済みの通報数
  onSceneCount: number;    // 現場到着の通報数
  clearedCount: number;    // 現場撤収の通報数
}

/**
 * 緊急通報管理サービス
 * 
 * バックエンドAPIとの通信を行い、緊急通報の作成、取得、更新などの
 * 操作を提供します。AngularのHttpClientを使用してRESTful APIと
 * 通信します。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Injectable({
  providedIn: 'root'
})
export class EmergencyCallService {
  private apiUrl = `${environment.apiUrl}/api/emergency-calls`;

  constructor(private http: HttpClient) {}

  /**
   * すべての緊急通報をページネーション付きで取得
   * 
   * @param page ページ番号（0から開始）
   * @param size 1ページあたりの件数
   * @returns 緊急通報のページ情報
   */
  getAllCalls(page: number = 0, size: number = 20): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<any>(this.apiUrl, { params });
  }

  /**
   * IDによる緊急通報の取得
   * 
   * @param id 緊急通報のID
   * @returns 緊急通報情報
   */
  getCallById(id: number): Observable<EmergencyCall> {
    return this.http.get<EmergencyCall>(`${this.apiUrl}/${id}`);
  }

  /**
   * 通報番号による緊急通報の取得
   * 
   * @param callNumber 通報番号
   * @returns 緊急通報情報
   */
  getCallByNumber(callNumber: string): Observable<EmergencyCall> {
    return this.http.get<EmergencyCall>(`${this.apiUrl}/call-number/${callNumber}`);
  }

  /**
   * ステータスによる緊急通報の取得
   * 
   * @param status 通報ステータス
   * @param page ページ番号
   * @param size 1ページあたりの件数
   * @returns 指定ステータスの緊急通報のページ情報
   */
  getCallsByStatus(status: CallStatus, page: number = 0, size: number = 20): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<any>(`${this.apiUrl}/status/${status}`, { params });
  }

  /**
   * アクティブな緊急通報の取得
   * 
   * @returns 現在処理中の緊急通報リスト
   */
  getActiveCalls(): Observable<EmergencyCall[]> {
    return this.http.get<EmergencyCall[]>(`${this.apiUrl}/active`);
  }

  /**
   * 日付範囲による緊急通報の取得
   * 
   * @param startDate 開始日時
   * @param endDate 終了日時
   * @returns 指定期間の緊急通報リスト
   */
  getCallsByDateRange(startDate: string, endDate: string): Observable<EmergencyCall[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    
    return this.http.get<EmergencyCall[]>(`${this.apiUrl}/date-range`, { params });
  }

  /**
   * 新しい緊急通報を作成
   * 
   * @param call 作成する緊急通報情報
   * @returns 作成された緊急通報情報
   */
  createCall(call: EmergencyCall): Observable<EmergencyCall> {
    return this.http.post<EmergencyCall>(this.apiUrl, call);
  }

  /**
   * 既存の緊急通報を更新
   * 
   * @param id 更新する緊急通報のID
   * @param call 更新する緊急通報情報
   * @returns 更新された緊急通報情報
   */
  updateCall(id: number, call: EmergencyCall): Observable<EmergencyCall> {
    return this.http.put<EmergencyCall>(`${this.apiUrl}/${id}`, call);
  }

  /**
   * 緊急通報のステータスを更新
   * 
   * @param id 更新する緊急通報のID
   * @param status 新しいステータス
   * @returns 更新された緊急通報情報
   */
  updateCallStatus(id: number, status: CallStatus): Observable<EmergencyCall> {
    const params = new HttpParams().set('status', status);
    return this.http.patch<EmergencyCall>(`${this.apiUrl}/${id}/status`, {}, { params });
  }

  /**
   * 消防署による緊急通報の取得
   * 
   * @param stationId 消防署のID
   * @returns 指定消防署が担当する緊急通報リスト
   */
  getCallsByStation(stationId: number): Observable<EmergencyCall[]> {
    return this.http.get<EmergencyCall[]>(`${this.apiUrl}/station/${stationId}`);
  }

  /**
   * ステータス別の緊急通報数を取得
   * 
   * @param status 通報ステータス
   * @returns 指定ステータスの緊急通報数
   */
  getCallCountByStatus(status: CallStatus): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/stats/status/${status}`);
  }

  /**
   * 総合的な通報統計情報を取得
   * 
   * @returns 通報統計情報
   */
  getCallSummary(): Observable<CallSummary> {
    return this.http.get<CallSummary>(`${this.apiUrl}/stats/summary`);
  }

  // Helper methods for UI
  /**
   * 通報ステータスに応じた色を取得
   * 
   * @param status 通報ステータス
   * @returns ステータスに対応する色コード
   */
  getStatusColor(status: CallStatus): string {
    switch (status) {
      case CallStatus.PENDING: return '#ff9800';
      case CallStatus.DISPATCHED: return '#2196f3';
      case CallStatus.EN_ROUTE: return '#2196f3';
      case CallStatus.ON_SCENE: return '#f44336';
      case CallStatus.CLEARED: return '#4caf50';
      case CallStatus.CANCELLED: return '#757575';
      default: return '#757575';
    }
  }

  /**
   * 優先度レベルに応じた色を取得
   * 
   * @param priority 優先度レベル
   * @returns 優先度に対応する色コード
   */
  getPriorityColor(priority: PriorityLevel): string {
    switch (priority) {
      case PriorityLevel.CRITICAL: return '#f44336';
      case PriorityLevel.HIGH: return '#ff9800';
      case PriorityLevel.MEDIUM: return '#2196f3';
      case PriorityLevel.LOW: return '#4caf50';
      default: return '#757575';
    }
  }

  /**
   * 事故事象の種類に応じたアイコンを取得
   * 
   * @param type 事故事象の種類
   * @returns 種類に対応するアイコンクラス名
   */
  getIncidentTypeIcon(type: IncidentType): string {
    switch (type) {
      case IncidentType.FIRE: return 'local_fire_department';
      case IncidentType.MEDICAL_EMERGENCY: return 'medical_services';
      case IncidentType.TRAFFIC_ACCIDENT: return 'car_crash';
      case IncidentType.HAZMAT: return 'warning';
      case IncidentType.RESCUE: return 'emergency';
      case IncidentType.FALSE_ALARM: return 'error';
      case IncidentType.OTHER: return 'help';
      default: return 'help';
    }
  }
}

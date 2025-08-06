import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface EmergencyCall {
  id?: number;
  callNumber: string;
  callerName?: string;
  callerPhone?: string;
  incidentAddress: string;
  latitude?: number;
  longitude?: number;
  incidentDescription?: string;
  incidentType: IncidentType;
  priorityLevel: PriorityLevel;
  status: CallStatus;
  receivedAt?: string;
  dispatchedAt?: string;
  arrivedAt?: string;
  clearedAt?: string;
  assignedStation?: any;
  assignedFirefighters?: any[];
}

export enum IncidentType {
  FIRE = 'FIRE',
  MEDICAL_EMERGENCY = 'MEDICAL_EMERGENCY',
  TRAFFIC_ACCIDENT = 'TRAFFIC_ACCIDENT',
  HAZMAT = 'HAZMAT',
  RESCUE = 'RESCUE',
  FALSE_ALARM = 'FALSE_ALARM',
  OTHER = 'OTHER'
}

export enum PriorityLevel {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export enum CallStatus {
  PENDING = 'PENDING',
  DISPATCHED = 'DISPATCHED',
  EN_ROUTE = 'EN_ROUTE',
  ON_SCENE = 'ON_SCENE',
  CLEARED = 'CLEARED',
  CANCELLED = 'CANCELLED'
}

export interface CallSummary {
  pendingCount: number;
  dispatchedCount: number;
  onSceneCount: number;
  clearedCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class EmergencyCallService {
  private apiUrl = `${environment.apiUrl}/api/emergency-calls`;

  constructor(private http: HttpClient) {}

  getAllCalls(page: number = 0, size: number = 20): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<any>(this.apiUrl, { params });
  }

  getCallById(id: number): Observable<EmergencyCall> {
    return this.http.get<EmergencyCall>(`${this.apiUrl}/${id}`);
  }

  getCallByNumber(callNumber: string): Observable<EmergencyCall> {
    return this.http.get<EmergencyCall>(`${this.apiUrl}/call-number/${callNumber}`);
  }

  getCallsByStatus(status: CallStatus, page: number = 0, size: number = 20): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<any>(`${this.apiUrl}/status/${status}`, { params });
  }

  getActiveCalls(): Observable<EmergencyCall[]> {
    return this.http.get<EmergencyCall[]>(`${this.apiUrl}/active`);
  }

  getCallsByDateRange(startDate: string, endDate: string): Observable<EmergencyCall[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    
    return this.http.get<EmergencyCall[]>(`${this.apiUrl}/date-range`, { params });
  }

  createCall(call: EmergencyCall): Observable<EmergencyCall> {
    return this.http.post<EmergencyCall>(this.apiUrl, call);
  }

  updateCall(id: number, call: EmergencyCall): Observable<EmergencyCall> {
    return this.http.put<EmergencyCall>(`${this.apiUrl}/${id}`, call);
  }

  updateCallStatus(id: number, status: CallStatus): Observable<EmergencyCall> {
    const params = new HttpParams().set('status', status);
    return this.http.patch<EmergencyCall>(`${this.apiUrl}/${id}/status`, {}, { params });
  }

  getCallsByStation(stationId: number): Observable<EmergencyCall[]> {
    return this.http.get<EmergencyCall[]>(`${this.apiUrl}/station/${stationId}`);
  }

  getCallCountByStatus(status: CallStatus): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/stats/status/${status}`);
  }

  getCallSummary(): Observable<CallSummary> {
    return this.http.get<CallSummary>(`${this.apiUrl}/stats/summary`);
  }

  // Helper methods for UI
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

  getPriorityColor(priority: PriorityLevel): string {
    switch (priority) {
      case PriorityLevel.CRITICAL: return '#f44336';
      case PriorityLevel.HIGH: return '#ff9800';
      case PriorityLevel.MEDIUM: return '#2196f3';
      case PriorityLevel.LOW: return '#4caf50';
      default: return '#757575';
    }
  }

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

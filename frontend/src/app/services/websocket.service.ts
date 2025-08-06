import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';

export interface WebSocketMessage {
  type: string;
  data: any;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private socket$: WebSocketSubject<WebSocketMessage> | null = null;
  private emergencyCallSubject = new Subject<any>();
  private dashboardUpdateSubject = new Subject<any>();
  private statusUpdateSubject = new Subject<any>();
  private systemAlertSubject = new Subject<any>();

  private readonly wsUrl = 'ws://localhost:8080/ws';

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

  disconnect(): void {
    if (this.socket$) {
      this.socket$.complete();
      this.socket$ = null;
    }
  }

  private handleMessage(message: WebSocketMessage): void {
    switch (message.type) {
      case 'EMERGENCY_CALL':
        this.emergencyCallSubject.next(message.data);
        break;
      case 'DASHBOARD_UPDATE':
        this.dashboardUpdateSubject.next(message.data);
        break;
      case 'STATUS_UPDATE':
        this.statusUpdateSubject.next(message.data);
        break;
      case 'SYSTEM_ALERT':
        this.systemAlertSubject.next(message.data);
        break;
      default:
        console.log('Unknown message type:', message.type);
    }
  }

  // Observable streams for different message types
  onEmergencyCall(): Observable<any> {
    return this.emergencyCallSubject.asObservable();
  }

  onDashboardUpdate(): Observable<any> {
    return this.dashboardUpdateSubject.asObservable();
  }

  onStatusUpdate(): Observable<any> {
    return this.statusUpdateSubject.asObservable();
  }

  onSystemAlert(): Observable<any> {
    return this.systemAlertSubject.asObservable();
  }

  // Send messages to server
  sendMessage(message: WebSocketMessage): void {
    if (this.socket$ && !this.socket$.closed) {
      this.socket$.next(message);
    }
  }

  // Subscribe to specific topics
  subscribeToTopic(topic: string): void {
    this.sendMessage({
      type: 'SUBSCRIBE',
      data: { topic }
    });
  }

  unsubscribeFromTopic(topic: string): void {
    this.sendMessage({
      type: 'UNSUBSCRIBE',
      data: { topic }
    });
  }

  // Send status updates
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

import { Component, OnInit, OnDestroy } from '@angular/core';
import { EmergencyCallService } from '../../services/emergency-call.service';
import { WebSocketService } from '../../services/websocket.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  // Dashboard data
  totalCalls = 0;
  activeCalls = 0;
  pendingCalls = 0;
  completedCalls = 0;
  
  // Recent calls
  recentCalls: any[] = [];
  
  // Loading state
  loading = true;
  
  // Chart data
  chartData: any = {
    labels: ['Pending', 'Dispatched', 'On Scene', 'Cleared'],
    datasets: [{
      data: [0, 0, 0, 0],
      backgroundColor: ['#ff9800', '#2196f3', '#f44336', '#4caf50']
    }]
  };

  constructor(
    private emergencyCallService: EmergencyCallService,
    private webSocketService: WebSocketService
  ) {}

  ngOnInit() {
    this.loadDashboardData();
    this.setupWebSocketListeners();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadDashboardData() {
    this.loading = true;
    
    // Load call summary
    this.emergencyCallService.getCallSummary()
      .pipe(takeUntil(this.destroy$))
      .subscribe(summary => {
        this.pendingCalls = summary.pendingCount;
        this.activeCalls = summary.dispatchedCount + summary.onSceneCount;
        this.completedCalls = summary.clearedCount;
        this.totalCalls = this.pendingCalls + this.activeCalls + this.completedCalls;
        
        // Update chart data
        this.chartData.datasets[0].data = [
          summary.pendingCount,
          summary.dispatchedCount,
          summary.onSceneCount,
          summary.clearedCount
        ];
        
        this.loading = false;
      });

    // Load recent calls
    this.emergencyCallService.getActiveCalls()
      .pipe(takeUntil(this.destroy$))
      .subscribe(calls => {
        this.recentCalls = calls.slice(0, 5);
      });
  }

  private setupWebSocketListeners() {
    this.webSocketService.onEmergencyCall()
      .pipe(takeUntil(this.destroy$))
      .subscribe(call => {
        this.loadDashboardData(); // Refresh data when new call comes in
      });

    this.webSocketService.onDashboardUpdate()
      .pipe(takeUntil(this.destroy$))
      .subscribe(update => {
        this.loadDashboardData(); // Refresh data when dashboard updates
      });
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'PENDING': return '#ff9800';
      case 'DISPATCHED': return '#2196f3';
      case 'EN_ROUTE': return '#2196f3';
      case 'ON_SCENE': return '#f44336';
      case 'CLEARED': return '#4caf50';
      default: return '#757575';
    }
  }

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

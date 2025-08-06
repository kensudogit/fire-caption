import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

// Material Modules
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatChipsModule } from '@angular/material/chips';
import { MatBadgeModule } from '@angular/material/badge';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTabsModule } from '@angular/material/tabs';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatGridListModule } from '@angular/material/grid-list';

// Components
import { AppComponent } from './app.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { EmergencyCallsComponent } from './components/emergency-calls/emergency-calls.component';
import { FireStationsComponent } from './components/fire-stations/fire-stations.component';
import { FirefightersComponent } from './components/firefighters/firefighters.component';
import { EquipmentComponent } from './components/equipment/equipment.component';
import { MapComponent } from './components/map/map.component';
import { CallDetailComponent } from './components/call-detail/call-detail.component';
import { LoginComponent } from './components/login/login.component';

// Services
import { EmergencyCallService } from './services/emergency-call.service';
import { WebSocketService } from './services/websocket.service';
import { AuthService } from './services/auth.service';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    EmergencyCallsComponent,
    FireStationsComponent,
    FirefightersComponent,
    EquipmentComponent,
    MapComponent,
    CallDetailComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ReactiveFormsModule,
    RouterModule.forRoot([
      { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'emergency-calls', component: EmergencyCallsComponent },
      { path: 'fire-stations', component: FireStationsComponent },
      { path: 'firefighters', component: FirefightersComponent },
      { path: 'equipment', component: EquipmentComponent },
      { path: 'map', component: MapComponent },
      { path: 'call/:id', component: CallDetailComponent },
      { path: 'login', component: LoginComponent }
    ]),
    MatToolbarModule,
    MatSidenavModule,
    MatButtonModule,
    MatIconModule,
    MatListModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatChipsModule,
    MatBadgeModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDialogModule,
    MatTabsModule,
    MatExpansionModule,
    MatGridListModule
  ],
  providers: [
    EmergencyCallService,
    WebSocketService,
    AuthService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

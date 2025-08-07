/**
 * 消防司令システム メインアプリケーションモジュール
 * 
 * このモジュールは消防司令システムのAngularアプリケーションの
 * ルートモジュールです。すべてのコンポーネント、サービス、
 * ルーティング、Material Designコンポーネントの設定を行います。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

// Material Design モジュール
// Angular MaterialのUIコンポーネントライブラリ
import { MatToolbarModule } from '@angular/material/toolbar';        // ツールバー
import { MatSidenavModule } from '@angular/material/sidenav';        // サイドナビゲーション
import { MatButtonModule } from '@angular/material/button';          // ボタン
import { MatIconModule } from '@angular/material/icon';              // アイコン
import { MatListModule } from '@angular/material/list';              // リスト
import { MatCardModule } from '@angular/material/card';              // カード
import { MatTableModule } from '@angular/material/table';            // テーブル
import { MatPaginatorModule } from '@angular/material/paginator';    // ページネーション
import { MatSortModule } from '@angular/material/sort';              // ソート
import { MatFormFieldModule } from '@angular/material/form-field';   // フォームフィールド
import { MatInputModule } from '@angular/material/input';            // 入力フィールド
import { MatSelectModule } from '@angular/material/select';          // セレクトボックス
import { MatDatepickerModule } from '@angular/material/datepicker';  // 日付選択
import { MatNativeDateModule } from '@angular/material/core';        // ネイティブ日付
import { MatChipsModule } from '@angular/material/chips';            // チップ
import { MatBadgeModule } from '@angular/material/badge';            // バッジ
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'; // プログレススピナー
import { MatSnackBarModule } from '@angular/material/snack-bar';     // スナックバー
import { MatDialogModule } from '@angular/material/dialog';          // ダイアログ
import { MatTabsModule } from '@angular/material/tabs';              // タブ
import { MatExpansionModule } from '@angular/material/expansion';    // 展開パネル
import { MatGridListModule } from '@angular/material/grid-list';     // グリッドリスト
import { MatMenuModule } from '@angular/material/menu';              // メニュー

// Chart.js
import { NgChartsModule } from 'ng2-charts';

// コンポーネント
// アプリケーションの各画面を構成するコンポーネント
import { AppComponent } from './app.component';                      // メインアプリケーションコンポーネント
import { DashboardComponent } from './components/dashboard/dashboard.component'; // ダッシュボード
import { EmergencyCallsComponent } from './components/emergency-calls/emergency-calls.component'; // 緊急通報管理
import { FireStationsComponent } from './components/fire-stations/fire-stations.component'; // 消防署管理
import { FirefightersComponent } from './components/firefighters/firefighters.component'; // 消防士管理
import { EquipmentComponent } from './components/equipment/equipment.component'; // 装備品管理
import { MapComponent } from './components/map/map.component';       // 地図表示
import { CallDetailComponent } from './components/call-detail/call-detail.component'; // 通報詳細
import { LoginComponent } from './components/login/login.component'; // ログイン

// サービス
// ビジネスロジックとデータ通信を担当するサービス
import { EmergencyCallService } from './services/emergency-call.service'; // 緊急通報管理サービス
import { WebSocketService } from './services/websocket.service';     // WebSocket通信サービス
import { AuthService } from './services/auth.service';               // 認証サービス

/**
 * アプリケーションのルートモジュール
 * 
 * すべてのコンポーネント、サービス、ルーティングを統合し、
 * 消防司令システムのフロントエンドアプリケーションを構成します。
 */
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
    BrowserModule,           // ブラウザサポート
    BrowserAnimationsModule, // アニメーションサポート
    HttpClientModule,        // HTTP通信
    ReactiveFormsModule,     // リアクティブフォーム
    RouterModule.forRoot([   // ルーティング設定
      { path: '', redirectTo: '/dashboard', pathMatch: 'full' },     // デフォルトルート
      { path: 'dashboard', component: DashboardComponent },          // ダッシュボード
      { path: 'emergency-calls', component: EmergencyCallsComponent }, // 緊急通報管理
      { path: 'fire-stations', component: FireStationsComponent },   // 消防署管理
      { path: 'firefighters', component: FirefightersComponent },    // 消防士管理
      { path: 'equipment', component: EquipmentComponent },          // 装備品管理
      { path: 'map', component: MapComponent },                      // 地図表示
      { path: 'call/:id', component: CallDetailComponent },          // 通報詳細
      { path: 'login', component: LoginComponent }                   // ログイン
    ]),
    // Material Design コンポーネント
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
    MatGridListModule,
    MatMenuModule,
    NgChartsModule
  ],
  providers: [
    EmergencyCallService,    // 緊急通報管理サービス
    WebSocketService,        // WebSocket通信サービス
    AuthService              // 認証サービス
  ],
  bootstrap: [AppComponent]  // 起動コンポーネント
})
export class AppModule { }

# 消防司令システム 処理フロー

## 概要

消防司令システムは以下の5段階の処理フローに基づいて設計されています：

1. **通報受付** → 2. **指令・部隊出動** → 3. **現場支援** → 4. **終了報告** → 5. **データ蓄積・分析**

## 処理フロー詳細

### 1. 通報受付 (Emergency Report)

**目的**: 緊急事態の通報を受付、初期情報を収集

**主要機能**:
- 通報番号の自動生成
- 通報者情報の記録
- 緊急事態の種類・場所・緊急度の判定
- リアルタイムでの通報状況追跡

**API エンドポイント**:
- `POST /api/emergency-reports` - 新規通報受付
- `GET /api/emergency-reports/{reportNumber}` - 通報詳細取得
- `PATCH /api/emergency-reports/{reportNumber}/status` - ステータス更新

**データモデル**: `EmergencyReport`
- 通報番号、通報者情報、緊急事態タイプ、場所、緊急度、ステータス

### 2. 指令・部隊出動 (Dispatch)

**目的**: 通報に基づいて適切な部隊を選定・出動指令

**主要機能**:
- 通報から指令の自動生成
- 最適な部隊の選定（距離、可用性、能力）
- 出動指令の発令
- 到着予想時間の計算

**API エンドポイント**:
- `GET /api/dispatches/{dispatchNumber}` - 指令詳細取得
- `PATCH /api/dispatches/{dispatchNumber}/status` - 指令ステータス更新
- `GET /api/dispatches/statistics` - 指令統計取得

**データモデル**: `Dispatch`, `DispatchUnit`
- 指令番号、指令タイプ、優先度、ステータス、部隊割り当て

### 3. 現場支援 (Scene Support)

**目的**: 必要に応じて追加支援を提供

**主要機能**:
- 自動支援要求の判定
- 支援タイプの決定（追加部隊、専門装備、医療支援等）
- 支援の承認・出動・完了管理
- コスト追跡

**API エンドポイント**:
- `POST /api/scene-supports` - 支援要求
- `PATCH /api/scene-supports/{supportId}/approve` - 支援承認
- `PATCH /api/scene-supports/{supportId}/complete` - 支援完了

**データモデル**: `SceneSupport`
- 支援タイプ、ステータス、要求・承認・完了時刻、コスト

### 4. 終了報告 (Completion Report)

**目的**: 事案の完了と詳細な報告書作成

**主要機能**:
- 事案完了の記録
- 詳細な活動報告
- 被害・救助状況の記録
- 教訓・改善点の記録

**API エンドポイント**:
- `POST /api/completion-reports` - 完了報告作成
- `GET /api/completion-reports/{reportId}` - 報告詳細取得
- `PUT /api/completion-reports/{reportId}` - 報告更新

**データモデル**: `CompletionReport`
- 完了タイプ、所要時間、被害状況、教訓、推奨事項

### 5. データ蓄積・分析 (Data Analysis)

**目的**: 蓄積されたデータの分析と洞察の提供

**主要機能**:
- 定期的な自動分析（日次・週次・月次）
- 応答時間・事案件数・コスト分析
- トレンド分析・予測モデリング
- パフォーマンス指標の算出

**API エンドポイント**:
- `GET /api/data-analyses` - 分析結果一覧
- `GET /api/data-analyses/{analysisId}` - 分析詳細取得
- `POST /api/data-analyses/perform` - 手動分析実行

**データモデル**: `DataAnalysis`
- 分析タイプ、期間、統計データ、洞察、推奨事項

## 統合API

### ダッシュボード
- `GET /api/fire-command/dashboard` - 全体状況の一覧表示

### ワークフロー管理
- `GET /api/fire-command/workflow/{reportNumber}` - 特定事案の進行状況
- `POST /api/fire-command/workflow/{reportNumber}/next` - 次の段階への進行

### リアルタイム更新
- `GET /api/fire-command/realtime` - 最新情報の取得

### システム監視
- `GET /api/fire-command/health` - システム健全性チェック

## 技術的特徴

### パフォーマンス最適化
- **キャッシュ**: Redis/EhCacheによる高速データアクセス
- **非同期処理**: `@Async`による並行処理
- **データベース最適化**: インデックス、クエリ最適化

### リアルタイム通信
- **WebSocket**: リアルタイム更新通知
- **イベント駆動**: 各段階での自動トリガー

### スケーラビリティ
- **マイクロサービス対応**: 段階別の独立したサービス
- **水平スケーリング**: 負荷分散対応

### 監視・ログ
- **Spring Boot Actuator**: システム監視
- **Micrometer**: メトリクス収集
- **Prometheus**: メトリクス可視化

## 処理フロー図

```
通報受付 → 指令・部隊出動 → 現場支援 → 終了報告 → データ蓄積・分析
    ↓           ↓            ↓          ↓           ↓
  受付番号    指令番号     支援ID     報告番号     分析ID
    ↓           ↓            ↓          ↓           ↓
  自動生成    部隊選定     支援判定    詳細記録    定期分析
    ↓           ↓            ↓          ↓           ↓
  リアルタイム  到着予測     コスト管理   教訓抽出    洞察提供
```

## 運用フロー

1. **通報受付**: 119番通報や自動検知システムからの情報を受付
2. **自動指令**: 通報内容に基づいて最適な部隊を自動選定・指令
3. **状況監視**: リアルタイムで部隊の位置・状況を追跡
4. **支援判断**: 必要に応じて追加支援を自動判定・要求
5. **完了処理**: 事案完了後の詳細報告・データ蓄積
6. **分析活用**: 蓄積データの分析による運用改善

この処理フローにより、消防活動の効率化と安全性向上を実現します。

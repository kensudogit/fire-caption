# 消防司令システム パフォーマンス最適化

## 概要

消防司令システムの実行性能を最大限に向上させるため、複数の最適化技術を実装しています。

## 最適化項目

### 1. データベース最適化

#### HikariCP 接続プール設定
```yaml
hikari:
  maximum-pool-size: 50          # 最大接続数
  minimum-idle: 10               # 最小アイドル接続数
  connection-timeout: 20000      # 接続タイムアウト
  idle-timeout: 300000           # アイドルタイムアウト
  max-lifetime: 1200000          # 最大ライフタイム
  leak-detection-threshold: 30000 # リーク検出閾値
```

#### Hibernate 最適化
```yaml
hibernate:
  jdbc:
    batch_size: 100              # バッチサイズ
    batch_versioned_data: true   # バージョン付きデータのバッチ処理
  order_inserts: true            # 挿入順序最適化
  order_updates: true            # 更新順序最適化
  connection:
    provider_disables_autocommit: true # 自動コミット無効化
```

### 2. キャッシュ最適化

#### EhCache 設定
- **Emergency Reports**: 5,000エントリ、15分TTL
- **Dispatches**: 3,000エントリ、10分TTL
- **Scene Supports**: 2,000エントリ、20分TTL
- **Data Analyses**: 1,000エントリ、2時間TTL
- **Statistics**: 100エントリ、5分TTL

#### キャッシュ戦略
- **L1 Cache**: Hibernate セッションキャッシュ
- **L2 Cache**: EhCache による分散キャッシュ
- **Query Cache**: 頻繁に実行されるクエリのキャッシュ
- **Application Cache**: アプリケーションレベルのキャッシュ

### 3. 非同期処理最適化

#### スレッドプール設定
```java
// メイン非同期処理
core-size: 16
max-size: 32
queue-capacity: 200

// データベース処理
core-size: 8
max-size: 16
queue-capacity: 100

// 分析処理
core-size: 4
max-size: 8
queue-capacity: 50
```

#### 非同期処理の活用
- 通報受付後の自動指令処理
- データ分析の定期実行
- リアルタイム更新通知
- バッチ処理

### 4. JVM 最適化

#### メモリ設定
```properties
org.gradle.jvmargs=-Xmx8g -Xms2g -XX:MaxMetaspaceSize=2g
```

#### GC 最適化
- **G1GC**: 低レイテンシーガベージコレクタ
- **MaxGCPauseMillis**: 200ms
- **HeapDumpOnOutOfMemoryError**: メモリダンプ有効化

### 5. Web サーバー最適化

#### Tomcat 設定
```yaml
tomcat:
  threads:
    max: 200                     # 最大スレッド数
    min-spare: 10                # 最小スパアスレッド
  max-connections: 8192          # 最大接続数
  accept-count: 100              # 受け入れ待ち数
```

#### 圧縮設定
```yaml
compression:
  enabled: true
  mime-types: text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json,application/xml
  min-response-size: 512
```

### 6. 監視・メトリクス

#### Micrometer メトリクス
- **応答時間**: REST API エンドポイントの応答時間
- **データベースクエリ時間**: クエリ実行時間
- **キャッシュヒット率**: 各キャッシュのヒット率
- **メモリ使用量**: JVM メモリ使用状況
- **スレッド数**: アクティブスレッド数

#### Prometheus 統合
- メトリクスの自動収集
- カスタムメトリクスの定義
- アラート設定

### 7. AOP によるパフォーマンス監視

#### 監視対象
- **サービス層**: 各サービスの実行時間
- **リポジトリ層**: データベースクエリ時間
- **コントローラー層**: REST API 応答時間
- **キャッシュ操作**: キャッシュアクセス時間
- **非同期処理**: 非同期タスクの実行時間

#### 自動カウンター
- 通報受付数
- 指令発令数
- 現場支援要求数

## パフォーマンス指標

### 目標値
- **応答時間**: 平均 < 100ms
- **データベースクエリ**: 平均 < 50ms
- **キャッシュヒット率**: > 80%
- **メモリ使用率**: < 70%
- **スループット**: > 1000 req/sec

### 監視ダッシュボード
- **Grafana**: リアルタイムメトリクス表示
- **Prometheus**: メトリクス収集・保存
- **Spring Boot Actuator**: ヘルスチェック・メトリクス

## 最適化効果

### 期待される改善
1. **応答時間**: 50-70% 短縮
2. **スループット**: 3-5倍向上
3. **メモリ使用量**: 30-40% 削減
4. **データベース負荷**: 60-80% 軽減
5. **キャッシュ効率**: 80-90% ヒット率

### スケーラビリティ
- **水平スケーリング**: 複数インスタンス対応
- **垂直スケーリング**: リソース増強対応
- **負荷分散**: ロードバランサー対応

## 運用監視

### 定期監視項目
- **システムリソース**: CPU、メモリ、ディスク
- **アプリケーションメトリクス**: 応答時間、エラー率
- **データベース性能**: クエリ時間、接続数
- **キャッシュ効率**: ヒット率、サイズ

### アラート設定
- **応答時間超過**: > 500ms
- **メモリ使用率**: > 80%
- **エラー率**: > 5%
- **データベース接続**: プール枯渇

## 今後の最適化計画

### 短期（1-3ヶ月）
- Redis クラスタ化
- データベースインデックス最適化
- クエリ最適化

### 中期（3-6ヶ月）
- マイクロサービス化
- コンテナ化（Docker/Kubernetes）
- CDN 導入

### 長期（6ヶ月以上）
- AI/ML による予測最適化
- エッジコンピューティング
- リアルタイムストリーミング処理

この最適化により、消防司令システムは高負荷環境でも安定した性能を発揮し、緊急時の迅速な対応を可能にします。

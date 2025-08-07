/**
 * 開発環境設定
 * 
 * 消防司令システムの開発環境用の設定ファイルです。
 * 開発時のAPIエンドポイント、WebSocket接続先などの
 * 環境固有の設定を定義します。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */

export const environment = {
  production: false,                    // 本番環境フラグ（開発時はfalse）
  apiUrl: 'http://localhost:8080',      // バックエンドAPIのベースURL
  wsUrl: 'ws://localhost:8080/ws'       // WebSocket接続URL
};

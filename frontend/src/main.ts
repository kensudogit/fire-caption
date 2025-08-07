/**
 * 消防司令システム フロントエンド メインエントリーポイント
 * 
 * このファイルはAngularアプリケーションの起動エントリーポイントです。
 * ブラウザ環境でAngularアプリケーションをブートストラップし、
 * 消防司令システムのフロントエンドを開始します。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */

import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';

// Angularアプリケーションのブートストラップ
// ブラウザ環境でAppModuleを起動し、エラーが発生した場合はコンソールに出力
platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));

package com.firecaptain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 消防司令システムのメインアプリケーションクラス
 * 
 * このクラスは消防司令システムのエントリーポイントとして機能し、
 * Spring Bootアプリケーションの起動と設定を行います。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@SpringBootApplication
@EnableCaching // キャッシュ機能を有効化（パフォーマンス向上のため）
@EnableAsync // 非同期処理を有効化（レスポンス性向上のため）
@EnableScheduling // スケジュール処理を有効化（定期タスク実行のため）
public class FireCaptainApplication {

    /**
     * アプリケーションのメインメソッド
     * 
     * Spring Bootアプリケーションを起動し、消防司令システムを開始します。
     * 
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(FireCaptainApplication.class, args);
    }
}

package com.firecaptain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket設定クラス
 * 
 * 消防司令システムのリアルタイム通信を担当するWebSocketの設定を行います。
 * STOMPプロトコルを使用したメッセージブローカーの設定と、
 * WebSocketエンドポイントの登録を行います。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * メッセージブローカーの設定
     * 
     * STOMPメッセージブローカーの設定を行い、トピックベースの
     * メッセージングとユーザー固有のメッセージングを有効化します。
     * 
     * @param config メッセージブローカー設定レジストリ
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // ブローカーエンドポイントの設定
        config.setApplicationDestinationPrefixes("/app"); // アプリケーション宛てメッセージのプレフィックス
        config.setUserDestinationPrefix("/user"); // ユーザー宛てメッセージのプレフィックス
    }

    /**
     * STOMPエンドポイントの登録
     * 
     * WebSocket接続のエンドポイントを登録し、CORS設定と
     * SockJSフォールバックを有効化します。
     * 
     * @param registry STOMPエンドポイントレジストリ
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // WebSocketエンドポイントのパス
                .setAllowedOriginPatterns("*") // すべてのオリジンからのアクセスを許可
                .withSockJS(); // SockJSフォールバックを有効化
    }
}

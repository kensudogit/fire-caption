package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * エンティティの基底クラス
 * 
 * すべてのエンティティクラスが継承する基底クラスです。
 * 共通のフィールド（ID、作成日時、更新日時、バージョン）と
 * 監査機能を提供します。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * エンティティの一意識別子
     * 自動採番により生成されます
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * エンティティの作成日時
     * 作成時に自動設定され、更新不可
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * エンティティの最終更新日時
     * 更新時に自動設定されます
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 楽観的ロック用のバージョン番号
     * 同時更新の競合を検出するために使用
     */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * エンティティ作成時の前処理
     * 作成日時と更新日時を現在時刻に設定
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * エンティティ更新時の前処理
     * 更新日時を現在時刻に設定
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

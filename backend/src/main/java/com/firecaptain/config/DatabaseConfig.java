package com.firecaptain.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * データベース設定クラス
 * 
 * 消防司令システムのデータベース接続とJPA設定を管理します。
 * HikariCPコネクションプールの最適化設定、Hibernateの
 * パフォーマンス設定、トランザクション管理の設定を行います。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.firecaptain.repository")
public class DatabaseConfig {

    /**
     * データソースプロパティの設定
     * 
     * application.propertiesからデータベース接続情報を読み込みます。
     * 
     * @return データソースプロパティ
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 高性能データソースの設定
     * 
     * HikariCPコネクションプールを使用した高性能なデータソースを
     * 設定します。接続プールのサイズ、タイムアウト、バッチ処理の
     * 最適化設定を含みます。
     * 
     * @return 最適化されたデータソース
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSourceProperties().getUrl());
        config.setUsername(dataSourceProperties().getUsername());
        config.setPassword(dataSourceProperties().getPassword());
        config.setDriverClassName(dataSourceProperties().getDriverClassName());

        // コネクションプールのパフォーマンス最適化設定
        config.setMaximumPoolSize(50); // 最大プールサイズ
        config.setMinimumIdle(10); // 最小アイドル接続数
        config.setConnectionTimeout(20000); // 接続タイムアウト（20秒）
        config.setIdleTimeout(300000); // アイドルタイムアウト（5分）
        config.setMaxLifetime(1200000); // 最大ライフタイム（20分）
        config.setLeakDetectionThreshold(30000); // リーク検出閾値（30秒）
        config.setConnectionTestQuery("SELECT 1"); // 接続テストクエリ
        config.setValidationTimeout(5000); // 検証タイムアウト（5秒）
        config.setRegisterMbeans(true); // JMX監視を有効化

        // MySQL/PostgreSQLの追加最適化設定
        config.addDataSourceProperty("cachePrepStmts", "true"); // プリペアドステートメントのキャッシュ
        config.addDataSourceProperty("prepStmtCacheSize", "250"); // プリペアドステートメントキャッシュサイズ
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); // キャッシュするSQLの最大長
        config.addDataSourceProperty("useServerPrepStmts", "true"); // サーバーサイドプリペアドステートメント
        config.addDataSourceProperty("useLocalSessionState", "true"); // ローカルセッション状態の使用
        config.addDataSourceProperty("rewriteBatchedStatements", "true"); // バッチステートメントの書き換え
        config.addDataSourceProperty("cacheResultSetMetadata", "true"); // 結果セットメタデータのキャッシュ
        config.addDataSourceProperty("cacheServerConfiguration", "true"); // サーバー設定のキャッシュ
        config.addDataSourceProperty("elideSetAutoCommits", "true"); // 自動コミット設定の最適化
        config.addDataSourceProperty("maintainTimeStats", "false"); // 時間統計の無効化

        return new HikariDataSource(config);
    }

    /**
     * EntityManagerFactoryの設定
     * 
     * JPAエンティティマネージャーファクトリを設定し、
     * Hibernateのパフォーマンス最適化を行います。
     * 
     * @return 設定されたEntityManagerFactory
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.firecaptain.entity"); // エンティティパッケージの指定

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false); // DDL自動生成を無効化
        vendorAdapter.setShowSql(false); // SQLログ出力を無効化
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect"); // PostgreSQL方言
        em.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.show_sql", "false"); // SQLログ出力を無効化
        properties.setProperty("hibernate.format_sql", "false"); // SQLフォーマットを無効化
        properties.setProperty("hibernate.jdbc.batch_size", "100"); // バッチサイズ
        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true"); // バージョン付きデータのバッチ処理
        properties.setProperty("hibernate.jdbc.batch.builder", "legacy"); // レガシーバッチビルダー
        properties.setProperty("hibernate.order_inserts", "true"); // 挿入の順序付け
        properties.setProperty("hibernate.order_updates", "true"); // 更新の順序付け
        properties.setProperty("hibernate.connection.provider_disables_autocommit", "true"); // 自動コミット無効化

        // セカンドレベルキャッシュの設定
        properties.setProperty("hibernate.cache.use_second_level_cache", "true");
        properties.setProperty("hibernate.cache.use_query_cache", "true");
        properties.setProperty("hibernate.cache.region.factory_class",
                "org.hibernate.cache.jcache.JCacheRegionFactory");
        properties.setProperty("hibernate.jcache.provider", "org.ehcache.jsr107.EhcacheCachingProvider");
        properties.setProperty("hibernate.jcache.uri", "classpath:ehcache.xml");

        // 統計情報
        properties.setProperty("hibernate.statistics.enabled", "true");
        properties.setProperty("hibernate.generate_statistics", "true");

        // パフォーマンス最適化
        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true");
        properties.setProperty("hibernate.connection.provider_disables_autocommit", "true");
        properties.setProperty("hibernate.jdbc.batch.builder", "legacy");

        em.setJpaProperties(properties);
        return em;
    }

    /**
     * トランザクションマネージャー設定
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }
}

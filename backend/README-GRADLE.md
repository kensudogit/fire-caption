# Fire Captain Backend - Gradle 設定ガイド

## 📋 概要

このプロジェクトは包括的なGradle設定を使用して、高性能で保守性の高いSpring Bootアプリケーションを構築します。

## 🛠️ 使用プラグイン

### 基本プラグイン
- **java**: Javaコンパイルとテスト
- **org.springframework.boot**: Spring Bootアプリケーション
- **io.spring.dependency-management**: Spring依存関係管理

### コード品質プラグイン
- **org.sonarqube**: コード品質分析
- **jacoco**: コードカバレッジ測定
- **checkstyle**: コーディング規約チェック
- **com.github.spotbugs**: バグ検出

### 開発支援プラグイン
- **org.flywaydb**: データベースマイグレーション
- **com.github.ben-manes.versions**: 依存関係バージョン管理

## 📦 依存関係管理

### バージョン変数
```gradle
ext {
    mapstructVersion = '1.5.5.Final'
    lombokVersion = '1.18.30'
    jjwtVersion = '0.12.3'
    postgresqlVersion = '42.7.1'
    ehcacheVersion = '3.10.8'
    testcontainersVersion = '1.19.3'
}
```

### 主要依存関係
- **Spring Boot Starters**: Web, Data JPA, Security, WebSocket, Cache, Actuator
- **Database**: PostgreSQL, Flyway
- **Cache**: EhCache, JCache API
- **Security**: JWT
- **Mapping**: MapStruct
- **Utilities**: Lombok, Apache Commons, Jackson
- **Monitoring**: Micrometer, Prometheus
- **Testing**: TestContainers, Mockito

## 🔧 設定詳細

### Java設定
```gradle
java {
    sourceCompatibility = '17'
    targetCompatibility = '17'
}
```

### MapStruct設定
```gradle
compileJava {
    options.compilerArgs = [
        '-Amapstruct.defaultComponentModel=spring',
        '-Amapstruct.verbose=true'
    ]
}
```

### Checkstyle設定
```gradle
checkstyle {
    toolVersion = '10.12.5'
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    configProperties = [
        'baseDir': "${rootDir}"
    ]
}
```

### SpotBugs設定
```gradle
spotbugs {
    effort = 'max'
    reportLevel = 'medium'
    excludeFilter = file("${rootDir}/config/spotbugs/exclude.xml")
}
```

### JaCoCo設定
```gradle
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.80
            }
        }
        rule {
            limit {
                counter = 'BRANCH'
                value = 'COVEREDRATIO'
                minimum = 0.70
            }
        }
    }
}
```

### SonarQube設定
```gradle
sonarqube {
    properties {
        property 'sonar.projectKey', 'fire-captain-backend'
        property 'sonar.projectName', 'Fire Captain Backend'
        property 'sonar.host.url', 'http://localhost:9000'
        property 'sonar.java.source', '17'
        property 'sonar.coverage.jacoco.xmlReportPaths', "${buildDir}/reports/jacoco/test/jacocoTestReport.xml"
    }
}
```

### Flyway設定
```gradle
flyway {
    url = 'jdbc:postgresql://localhost:5432/fire_captain_db'
    user = 'fire_captain_user'
    password = 'fire_captain_password'
    locations = ['classpath:db/migration']
    baselineOnMigrate = true
}
```

## 🚀 カスタムタスク

### バージョン情報表示
```bash
./gradlew printVersion
```

### APIドキュメント生成
```bash
./gradlew generateApiDocs
```

### Dockerビルド
```bash
./gradlew dockerBuild
```

## 📊 ビルド最適化

### 並列実行
```gradle
tasks.withType(Test) {
    maxParallelForks = Runtime.runtime.availableProcessors().intdiv(2) ?: 1
    forkEvery = 100
}
```

### JVM最適化
```gradle
tasks.withType(JavaExec) {
    jvmArgs += [
        '-XX:+UseG1GC',
        '-XX:MaxGCPauseMillis=200',
        '-Xms512m',
        '-Xmx2g'
    ]
}
```

## 🔍 コード品質チェック

### 全チェック実行
```bash
./gradlew check
```

### 個別チェック
```bash
# Checkstyle
./gradlew checkstyleMain

# SpotBugs
./gradlew spotbugsMain

# JaCoCo
./gradlew jacocoTestReport

# SonarQube
./gradlew sonarqube
```

## 📈 パフォーマンス監視

### Gradle設定
```properties
# gradle.properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
org.gradle.daemon=true
org.gradle.daemon.idletimeout=10800000
```

### ビルド時間測定
```bash
./gradlew build --profile
```

## 🧪 テスト設定

### テスト実行
```bash
# 全テスト
./gradlew test

# 特定テストクラス
./gradlew test --tests EmergencyCallServiceTest

# テストカバレッジ付き
./gradlew test jacocoTestReport
```

### TestContainers設定
```gradle
testImplementation 'org.testcontainers:junit-jupiter'
testImplementation 'org.testcontainers:postgresql'
```

## 📋 依存関係管理

### バージョン確認
```bash
./gradlew dependencies
```

### 依存関係更新確認
```bash
./gradlew dependencyUpdates
```

### 依存関係ロック
```gradle
dependencyLocking {
    lockAllConfigurations()
}
```

## 🔧 環境別設定

### 開発環境
```bash
./gradlew bootRun
```

### 本番環境
```bash
./gradlew bootJar
java -jar build/libs/fire-captain-backend-0.0.1-SNAPSHOT.jar
```

### Docker環境
```bash
./gradlew dockerBuild
docker run -p 8080:8080 fire-captain-backend:0.0.1-SNAPSHOT
```

## 📊 レポート生成

### 生成されるレポート
- **Checkstyle**: `build/reports/checkstyle/`
- **SpotBugs**: `build/reports/spotbugs/`
- **JaCoCo**: `build/reports/jacoco/`
- **Test**: `build/reports/tests/`

### レポート確認
```bash
# HTMLレポートを開く
open build/reports/checkstyle/main/checkstyle-report.html
open build/reports/spotbugs/main/spotbugs.html
open build/reports/jacoco/test/html/index.html
```

## 🚨 トラブルシューティング

### よくある問題

#### メモリ不足
```bash
export GRADLE_OPTS="-Xmx4g -XX:MaxMetaspaceSize=1g"
./gradlew build
```

#### 依存関係解決エラー
```bash
./gradlew --refresh-dependencies build
```

#### キャッシュクリア
```bash
./gradlew clean build
```

#### Daemon再起動
```bash
./gradlew --stop
./gradlew build
```

## 📚 参考資料

- [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)
- [Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/)
- [JaCoCo Gradle Plugin](https://docs.gradle.org/current/userguide/jacoco_plugin.html)
- [Checkstyle Gradle Plugin](https://docs.gradle.org/current/userguide/checkstyle_plugin.html)
- [SpotBugs Gradle Plugin](https://spotbugs.readthedocs.io/en/latest/gradle.html)

FROM openjdk:17-jdk-slim

WORKDIR /app

# Gradle wrapperとソースコードをコピー
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Gradle wrapperに実行権限を付与
RUN chmod +x gradlew

# 依存関係をダウンロード
RUN ./gradlew dependencies

# アプリケーションをビルド
RUN ./gradlew build -x test

# JARファイルを実行用ディレクトリにコピー
RUN ls -la build/libs/ && cp build/libs/fire-captain-backend-0.0.1-SNAPSHOT.jar app.jar

# ポート8080を公開
EXPOSE 8080

# アプリケーションを起動
ENTRYPOINT ["java", "-jar", "app.jar"]

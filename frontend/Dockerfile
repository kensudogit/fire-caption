# Build stage
FROM node:18-alpine as build

WORKDIR /app

# package.jsonとpackage-lock.jsonをコピー
COPY package*.json ./

# すべての依存関係をインストール（ビルドにdevDependenciesが必要）
RUN if [ -f package-lock.json ]; then npm ci; else npm install; fi

# ソースコードをコピー
COPY . .

# アプリケーションをビルド
RUN npm run build

# Production stage
FROM nginx:alpine

# ビルドされたファイルをnginxにコピー
COPY --from=build /app/dist/fire-captain-frontend /usr/share/nginx/html

# nginx設定をコピー
COPY nginx.conf /etc/nginx/nginx.conf

# ポート80を公開
EXPOSE 80

# nginxを起動
CMD ["nginx", "-g", "daemon off;"]

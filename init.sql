-- 消防司令システム データベース初期化スクリプト
-- 
-- このファイルは消防司令システムのPostgreSQLデータベースを初期化し、
-- 必要な拡張機能、インデックス、サンプルデータを設定します。
-- 
-- @author FireCaptain Team
-- @version 1.0

-- Fire Captain System Database Initialization

-- PostGIS拡張機能の有効化（空間データ処理のため）
-- 位置情報ベースの検索や距離計算に必要
CREATE EXTENSION IF NOT EXISTS postgis;

-- パフォーマンス向上のためのインデックス作成
-- 空間データのインデックス（位置情報検索の高速化）
CREATE INDEX IF NOT EXISTS idx_fire_stations_location ON fire_stations USING GIST (ST_SetSRID(ST_MakePoint(longitude, latitude), 4326));
CREATE INDEX IF NOT EXISTS idx_emergency_calls_location ON emergency_calls USING GIST (ST_SetSRID(ST_MakePoint(longitude, latitude), 4326));

-- 通常のインデックス（検索・ソートの高速化）
CREATE INDEX IF NOT EXISTS idx_emergency_calls_status ON emergency_calls(status);        -- ステータス別検索
CREATE INDEX IF NOT EXISTS idx_emergency_calls_received_at ON emergency_calls(received_at);  -- 日時別検索
CREATE INDEX IF NOT EXISTS idx_firefighters_station ON firefighters(fire_station_id);   -- 消防署別消防士検索
CREATE INDEX IF NOT EXISTS idx_equipment_station ON equipment(fire_station_id);         -- 消防署別装備検索

-- サンプルデータの挿入
-- 消防署のサンプルデータ
-- 実際の運用では、本番環境に適したデータに置き換えてください
INSERT INTO fire_stations (station_code, station_name, address, latitude, longitude, phone_number, email, station_type, capacity, is_active) VALUES
('FS001', 'Central Fire Station', '123 Main St, Downtown', 35.6762, 139.6503, '03-1234-5678', 'central@fire.gov', 'MAIN_STATION', 50, true),    -- 中央消防署
('FS002', 'North Fire Station', '456 North Ave, North District', 35.6862, 139.6603, '03-1234-5679', 'north@fire.gov', 'BRANCH_STATION', 30, true),  -- 北消防署
('FS003', 'South Fire Station', '789 South Blvd, South District', 35.6662, 139.6403, '03-1234-5680', 'south@fire.gov', 'BRANCH_STATION', 25, true); -- 南消防署

-- 消防士のサンプルデータ
-- 各消防署に配属される消防士の情報
INSERT INTO firefighters (employee_id, first_name, last_name, email, phone_number, rank, status, fire_station_id) VALUES
('FF001', 'John', 'Smith', 'john.smith@fire.gov', '090-1234-5678', 'CAPTAIN', 'ACTIVE', 1),      -- 隊長
('FF002', 'Jane', 'Doe', 'jane.doe@fire.gov', '090-1234-5679', 'LIEUTENANT', 'ACTIVE', 1),       -- 副隊長
('FF003', 'Mike', 'Johnson', 'mike.johnson@fire.gov', '090-1234-5680', 'FIREFIGHTER', 'ACTIVE', 2); -- 消防士

-- 装備品のサンプルデータ
-- 各消防署に配備される消防車両や装備品の情報
INSERT INTO equipment (equipment_code, equipment_name, description, equipment_type, status, manufacturer, model_number, fire_station_id) VALUES
('EQ001', 'Fire Truck Alpha', 'Main firefighting vehicle', 'FIRE_TRUCK', 'AVAILABLE', 'FireTruck Co.', 'FT-2024', 1),     -- 消防車
('EQ002', 'Ambulance Beta', 'Emergency medical vehicle', 'AMBULANCE', 'AVAILABLE', 'Ambulance Inc.', 'AM-2024', 1),       -- 救急車
('EQ003', 'Ladder Truck Gamma', 'Aerial ladder vehicle', 'LADDER_TRUCK', 'AVAILABLE', 'Ladder Corp.', 'LT-2024', 2);     -- はしご車

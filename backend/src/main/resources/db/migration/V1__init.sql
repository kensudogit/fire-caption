-- Fire Captain System Database Initialization
-- Version: 1.0.0

-- Enable PostGIS extension for spatial data
CREATE EXTENSION IF NOT EXISTS postgis;

-- Create base entity table structure
CREATE TABLE IF NOT EXISTS fire_stations (
    id BIGSERIAL PRIMARY KEY,
    station_code VARCHAR(50) UNIQUE NOT NULL,
    station_name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    phone_number VARCHAR(20),
    email VARCHAR(255),
    station_type VARCHAR(50) NOT NULL,
    capacity INTEGER,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS firefighters (
    id BIGSERIAL PRIMARY KEY,
    employee_id VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(20),
    date_of_birth DATE,
    hire_date DATE,
    rank VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    fire_station_id BIGINT REFERENCES fire_stations(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS emergency_calls (
    id BIGSERIAL PRIMARY KEY,
    call_number VARCHAR(50) UNIQUE NOT NULL,
    caller_name VARCHAR(255),
    caller_phone VARCHAR(20),
    incident_address TEXT NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    incident_description TEXT,
    incident_type VARCHAR(50) NOT NULL,
    priority_level VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    received_at TIMESTAMP NOT NULL,
    dispatched_at TIMESTAMP,
    arrived_at TIMESTAMP,
    cleared_at TIMESTAMP,
    assigned_station_id BIGINT REFERENCES fire_stations(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS equipment (
    id BIGSERIAL PRIMARY KEY,
    equipment_code VARCHAR(50) UNIQUE NOT NULL,
    equipment_name VARCHAR(255) NOT NULL,
    description TEXT,
    equipment_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'AVAILABLE',
    manufacturer VARCHAR(255),
    model_number VARCHAR(100),
    serial_number VARCHAR(100),
    purchase_date DATE,
    last_maintenance_date DATE,
    next_maintenance_date DATE,
    location VARCHAR(255),
    fire_station_id BIGINT REFERENCES fire_stations(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    last_login TIMESTAMP,
    failed_login_attempts INTEGER DEFAULT 0,
    account_locked_until TIMESTAMP,
    fire_station_id BIGINT REFERENCES fire_stations(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    permission_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS user_permissions (
    user_id BIGINT REFERENCES users(id),
    permission_id BIGINT REFERENCES permissions(id),
    PRIMARY KEY (user_id, permission_id)
);

CREATE TABLE IF NOT EXISTS emergency_call_firefighters (
    emergency_call_id BIGINT REFERENCES emergency_calls(id),
    firefighter_id BIGINT REFERENCES firefighters(id),
    PRIMARY KEY (emergency_call_id, firefighter_id)
);

CREATE TABLE IF NOT EXISTS call_updates (
    id BIGSERIAL PRIMARY KEY,
    emergency_call_id BIGINT NOT NULL REFERENCES emergency_calls(id),
    firefighter_id BIGINT NOT NULL REFERENCES firefighters(id),
    update_text TEXT NOT NULL,
    update_type VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    location_latitude DOUBLE PRECISION,
    location_longitude DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS certifications (
    id BIGSERIAL PRIMARY KEY,
    firefighter_id BIGINT NOT NULL REFERENCES firefighters(id),
    certification_name VARCHAR(255) NOT NULL,
    issuing_authority VARCHAR(255) NOT NULL,
    certification_number VARCHAR(100),
    issue_date DATE NOT NULL,
    expiry_date DATE,
    certification_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_fire_stations_location ON fire_stations USING GIST (ST_SetSRID(ST_MakePoint(longitude, latitude), 4326));
CREATE INDEX IF NOT EXISTS idx_emergency_calls_location ON emergency_calls USING GIST (ST_SetSRID(ST_MakePoint(longitude, latitude), 4326));
CREATE INDEX IF NOT EXISTS idx_emergency_calls_status ON emergency_calls(status);
CREATE INDEX IF NOT EXISTS idx_emergency_calls_received_at ON emergency_calls(received_at);
CREATE INDEX IF NOT EXISTS idx_firefighters_station ON firefighters(fire_station_id);
CREATE INDEX IF NOT EXISTS idx_equipment_station ON equipment(fire_station_id);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_call_updates_emergency_call ON call_updates(emergency_call_id);
CREATE INDEX IF NOT EXISTS idx_certifications_firefighter ON certifications(firefighter_id);

-- Insert sample data
INSERT INTO fire_stations (station_code, station_name, address, latitude, longitude, phone_number, email, station_type, capacity, is_active) VALUES
('FS001', 'Central Fire Station', '123 Main St, Downtown', 35.6762, 139.6503, '03-1234-5678', 'central@fire.gov', 'MAIN_STATION', 50, true),
('FS002', 'North Fire Station', '456 North Ave, North District', 35.6862, 139.6603, '03-1234-5679', 'north@fire.gov', 'BRANCH_STATION', 30, true),
('FS003', 'South Fire Station', '789 South Blvd, South District', 35.6662, 139.6403, '03-1234-5680', 'south@fire.gov', 'BRANCH_STATION', 25, true)
ON CONFLICT (station_code) DO NOTHING;

-- Insert sample firefighters
INSERT INTO firefighters (employee_id, first_name, last_name, email, phone_number, rank, status, fire_station_id) VALUES
('FF001', 'John', 'Smith', 'john.smith@fire.gov', '090-1234-5678', 'CAPTAIN', 'ACTIVE', 1),
('FF002', 'Jane', 'Doe', 'jane.doe@fire.gov', '090-1234-5679', 'LIEUTENANT', 'ACTIVE', 1),
('FF003', 'Mike', 'Johnson', 'mike.johnson@fire.gov', '090-1234-5680', 'FIREFIGHTER', 'ACTIVE', 2)
ON CONFLICT (employee_id) DO NOTHING;

-- Insert sample equipment
INSERT INTO equipment (equipment_code, equipment_name, description, equipment_type, status, manufacturer, model_number, fire_station_id) VALUES
('EQ001', 'Fire Truck Alpha', 'Main firefighting vehicle', 'FIRE_TRUCK', 'AVAILABLE', 'FireTruck Co.', 'FT-2024', 1),
('EQ002', 'Ambulance Beta', 'Emergency medical vehicle', 'AMBULANCE', 'AVAILABLE', 'Ambulance Inc.', 'AM-2024', 1),
('EQ003', 'Ladder Truck Gamma', 'Aerial ladder vehicle', 'LADDER_TRUCK', 'AVAILABLE', 'Ladder Corp.', 'LT-2024', 2)
ON CONFLICT (equipment_code) DO NOTHING;

-- Insert sample permissions
INSERT INTO permissions (permission_name, description, resource, action) VALUES
('EMERGENCY_CALLS_READ', 'Read emergency calls', 'EMERGENCY_CALLS', 'READ'),
('EMERGENCY_CALLS_CREATE', 'Create emergency calls', 'EMERGENCY_CALLS', 'CREATE'),
('EMERGENCY_CALLS_UPDATE', 'Update emergency calls', 'EMERGENCY_CALLS', 'UPDATE'),
('EMERGENCY_CALLS_DELETE', 'Delete emergency calls', 'EMERGENCY_CALLS', 'DELETE'),
('FIREFIGHTERS_READ', 'Read firefighters', 'FIREFIGHTERS', 'READ'),
('FIREFIGHTERS_CREATE', 'Create firefighters', 'FIREFIGHTERS', 'CREATE'),
('FIREFIGHTERS_UPDATE', 'Update firefighters', 'FIREFIGHTERS', 'UPDATE'),
('FIREFIGHTERS_DELETE', 'Delete firefighters', 'FIREFIGHTERS', 'DELETE'),
('FIRE_STATIONS_READ', 'Read fire stations', 'FIRE_STATIONS', 'READ'),
('FIRE_STATIONS_CREATE', 'Create fire stations', 'FIRE_STATIONS', 'CREATE'),
('FIRE_STATIONS_UPDATE', 'Update fire stations', 'FIRE_STATIONS', 'UPDATE'),
('FIRE_STATIONS_DELETE', 'Delete fire stations', 'FIRE_STATIONS', 'DELETE'),
('EQUIPMENT_READ', 'Read equipment', 'EQUIPMENT', 'READ'),
('EQUIPMENT_CREATE', 'Create equipment', 'EQUIPMENT', 'CREATE'),
('EQUIPMENT_UPDATE', 'Update equipment', 'EQUIPMENT', 'UPDATE'),
('EQUIPMENT_DELETE', 'Delete equipment', 'EQUIPMENT', 'DELETE')
ON CONFLICT (permission_name) DO NOTHING;








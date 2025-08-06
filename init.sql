-- Fire Captain System Database Initialization

-- Enable PostGIS extension for spatial data
CREATE EXTENSION IF NOT EXISTS postgis;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_fire_stations_location ON fire_stations USING GIST (ST_SetSRID(ST_MakePoint(longitude, latitude), 4326));
CREATE INDEX IF NOT EXISTS idx_emergency_calls_location ON emergency_calls USING GIST (ST_SetSRID(ST_MakePoint(longitude, latitude), 4326));
CREATE INDEX IF NOT EXISTS idx_emergency_calls_status ON emergency_calls(status);
CREATE INDEX IF NOT EXISTS idx_emergency_calls_received_at ON emergency_calls(received_at);
CREATE INDEX IF NOT EXISTS idx_firefighters_station ON firefighters(fire_station_id);
CREATE INDEX IF NOT EXISTS idx_equipment_station ON equipment(fire_station_id);

-- Insert sample data
INSERT INTO fire_stations (station_code, station_name, address, latitude, longitude, phone_number, email, station_type, capacity, is_active) VALUES
('FS001', 'Central Fire Station', '123 Main St, Downtown', 35.6762, 139.6503, '03-1234-5678', 'central@fire.gov', 'MAIN_STATION', 50, true),
('FS002', 'North Fire Station', '456 North Ave, North District', 35.6862, 139.6603, '03-1234-5679', 'north@fire.gov', 'BRANCH_STATION', 30, true),
('FS003', 'South Fire Station', '789 South Blvd, South District', 35.6662, 139.6403, '03-1234-5680', 'south@fire.gov', 'BRANCH_STATION', 25, true);

-- Insert sample firefighters
INSERT INTO firefighters (employee_id, first_name, last_name, email, phone_number, rank, status, fire_station_id) VALUES
('FF001', 'John', 'Smith', 'john.smith@fire.gov', '090-1234-5678', 'CAPTAIN', 'ACTIVE', 1),
('FF002', 'Jane', 'Doe', 'jane.doe@fire.gov', '090-1234-5679', 'LIEUTENANT', 'ACTIVE', 1),
('FF003', 'Mike', 'Johnson', 'mike.johnson@fire.gov', '090-1234-5680', 'FIREFIGHTER', 'ACTIVE', 2);

-- Insert sample equipment
INSERT INTO equipment (equipment_code, equipment_name, description, equipment_type, status, manufacturer, model_number, fire_station_id) VALUES
('EQ001', 'Fire Truck Alpha', 'Main firefighting vehicle', 'FIRE_TRUCK', 'AVAILABLE', 'FireTruck Co.', 'FT-2024', 1),
('EQ002', 'Ambulance Beta', 'Emergency medical vehicle', 'AMBULANCE', 'AVAILABLE', 'Ambulance Inc.', 'AM-2024', 1),
('EQ003', 'Ladder Truck Gamma', 'Aerial ladder vehicle', 'LADDER_TRUCK', 'AVAILABLE', 'Ladder Corp.', 'LT-2024', 2);

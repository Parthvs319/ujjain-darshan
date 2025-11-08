-- create tables for initial entities
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id VARCHAR(255) NOT NULL,
  mobile VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(255),
  email VARCHAR(255),
  residing_city VARCHAR(255),
  user_type VARCHAR(50),
  verified BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP
);

CREATE TABLE cities (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE temples (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  address VARCHAR(500),
  city VARCHAR(255)
);

CREATE TABLE hotels (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  address VARCHAR(500),
  rating INT,
  city VARCHAR(255)
);

CREATE TABLE pujas (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id VARCHAR(255) NOT NULL,
  temple_id BIGINT NOT NULL,
  name VARCHAR(255),
  price DECIMAL(10,2),
  description VARCHAR(1000)
);

CREATE TABLE trips (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id VARCHAR(255) NOT NULL,
  user_id BIGINT NOT NULL,
  cab_details VARCHAR(1000),
  pickup_location VARCHAR(500),
  drop_location VARCHAR(500),
  start_date DATE,
  end_date DATE,
  hotel_ids_json CLOB,
  temple_ids_json CLOB,
  itinerary_json CLOB,
  paid BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS truck
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version BIGINT NOT NULL,
    register_plate_number VARCHAR(255) NOT NULL UNIQUE,
    business_id VARCHAR(255) NOT NULL UNIQUE,
    in_use BOOLEAN,
    start_period TIMESTAMP,
    end_period TIMESTAMP,
    length DOUBLE NOT NULL,
    height DOUBLE NOT NULL,
    weight DOUBLE NOT NULL
    );

CREATE TABLE IF NOT EXISTS trailer
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version BIGINT NOT NULL,
    register_plate_number VARCHAR(255) NOT NULL UNIQUE,
    business_id VARCHAR(255) NOT NULL UNIQUE,
    in_use BOOLEAN,
    cross_hitch BOOLEAN,
    start_period TIMESTAMP,
    end_period TIMESTAMP,
    length DOUBLE NOT NULL,
    height DOUBLE NOT NULL,
    weight DOUBLE NOT NULL
    );

CREATE TABLE IF NOT EXISTS truck_trailer
(
    truck_id BIGINT,
    trailer_id BIGINT,
    PRIMARY KEY (truck_id, trailer_id),
    FOREIGN KEY (truck_id) REFERENCES truck (id),
    FOREIGN KEY (trailer_id) REFERENCES trailer (id)
    );

INSERT INTO truck (version, register_plate_number, business_id, in_use, length, height, weight)
VALUES (1, 'TRK001', '52333a07-520e-465f-a6c2-5891080637e5', FALSE, 7.5, 3.2, 15000.0),
       (1, 'TRK002', 'fc936032-52e8-4ec0-b916-3bc4b49b956c', FALSE, 6.0, 2.8, 12000.0),
       (1, 'TRK003', 'cddf1383-6d8e-4f55-a39f-8a71477317d3', FALSE, 8.0, 3.5, 18000.0);

INSERT INTO trailer (version, register_plate_number, business_id, in_use, cross_hitch, length, height, weight)
VALUES (1, 'TRL001', '542602cf-97d5-4548-8831-55f21d35fcf4', FALSE, FALSE, 15.0, 4.0, 3500.0),
       (1, 'TRL002', 'a42c8aab-a60f-4a77-991e-d97c6248b33f', FALSE, FALSE, 12.5, 3.5, 3000.0),
       (1, 'TRL003', '5d41877e-a0c4-45e0-9b85-2de15e9352ee', FALSE, FALSE, 14.0, 3.8, 3200.0);

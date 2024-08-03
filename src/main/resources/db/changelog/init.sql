CREATE TABLE IF NOT EXISTS truck
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    version               BIGINT       NOT NULL,
    register_plate_number VARCHAR(255) NOT NULL UNIQUE,
    business_id           VARCHAR(255) NOT NULL UNIQUE,
    in_use                BOOLEAN,
    start_period_date     TIMESTAMP,
    end_period_date       TIMESTAMP,
    length                DOUBLE       NOT NULL,
    height                DOUBLE       NOT NULL,
    weight                DOUBLE       NOT NULL
);

CREATE TABLE IF NOT EXISTS trailer
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    version               BIGINT       NOT NULL,
    register_plate_number VARCHAR(255) NOT NULL UNIQUE,
    business_id           VARCHAR(255) NOT NULL UNIQUE,
    in_use                BOOLEAN,
    cross_hitch           BOOLEAN,
    start_period_date     TIMESTAMP,
    end_period_date       TIMESTAMP,
    length                DOUBLE       NOT NULL,
    height                DOUBLE       NOT NULL,
    weight                DOUBLE       NOT NULL
);

CREATE TABLE IF NOT EXISTS truck_trailer_history
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    version           BIGINT NOT NULL,
    truck_id          BIGINT,
    trailer_id        BIGINT,
    start_period_date TIMESTAMP,
    end_period_date   TIMESTAMP,
    CONSTRAINT fk_truck FOREIGN KEY (truck_id) REFERENCES truck (id),
    CONSTRAINT fk_trailer FOREIGN KEY (trailer_id) REFERENCES trailer (id)
);

INSERT INTO truck (version, register_plate_number, business_id, in_use, length, height, weight)
VALUES (1, 'TRK001', '52333a07-520e-465f-a6c2-5891080637e5', FALSE, 7.5, 3.2, 15000.0),
       (1, 'TRK002', 'fc936032-52e8-4ec0-b916-3bc4b49b956c', FALSE, 6.0, 2.8, 12000.0),
       (1, 'TRK003', 'cddf1383-6d8e-4f55-a39f-8a71477317d3', FALSE, 8.0, 3.5, 18000.0);

INSERT INTO trailer (version, register_plate_number, business_id, in_use, cross_hitch, length, height, weight)
VALUES (1, 'TRL001', '542602cf-97d5-4548-8831-55f21d35fcf4', FALSE, FALSE, 15.0, 4.0, 3500.0),
       (1, 'TRL002', 'a42c8aab-a60f-4a77-991e-d97c6248b33f', FALSE, FALSE, 12.5, 3.5, 3000.0),
       (1, 'TRL003', '5d41877e-a0c4-45e0-9b85-2de15e9352ee', TRUE, FALSE, 14.0, 3.8, 3200.0);

INSERT INTO trailer (version, register_plate_number, business_id, in_use, cross_hitch, start_period_date,
                     end_period_date, length, height, weight)
VALUES (1, 'ABC1234', 'e8a2f7c1-8b1d-4bfb-9d92-3a1b7c6e5071', TRUE, FALSE, '2024-06-01 08:00:00', '2024-06-15 08:00:00', 15.5, 4.2, 3200.0),
       (1, 'XYZ5678', 'f47b8c5a-b6d4-4e51-b4b9-a62bfa7d8f3c', FALSE, TRUE, '2024-06-05 09:00:00', '2024-06-19 09:00:00', 20.0, 5.0, 4500.0),
       (1, 'LMN9101', '21e5d687-3b69-462a-a4b4-ff7a4d8e9a24', TRUE, TRUE, '2024-06-10 07:00:00', '2024-06-24 07:00:00', 18.7, 4.5, 3800.0),
       (1, 'OPQ2345', '9cdbb6b1-7a47-4f37-9c5e-1c1f98cbb6eb', FALSE, FALSE, '2024-06-15 10:00:00', '2024-06-29 10:00:00', 22.3, 4.8, 5000.0),
       (1, 'RST6789', 'd2e9c908-88b5-4c82-9d29-cbcbf6a4e1a9', TRUE, TRUE, '2024-06-20 06:00:00', '2024-07-04 06:00:00', 19.8, 5.2, 4100.0);

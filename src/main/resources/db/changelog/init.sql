CREATE TABLE IF NOT EXISTS truck
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    version               BIGINT       NOT NULL,
    register_plate_number VARCHAR(255) NOT NULL UNIQUE,
    business_id           VARCHAR(255) NOT NULL UNIQUE,
    in_use                BOOLEAN,
    start_period_date     TIMESTAMP,
    end_period_date       TIMESTAMP,
    current_trailer_business_id VARCHAR(255)
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
    current_truck_business_is VARCHAR(255)
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

INSERT INTO truck (version, register_plate_number, business_id, in_use)
VALUES (1, 'TRK001', '52333a07-520e-465f-a6c2-5891080637e5', FALSE),
       (1, 'TRK002', 'fc936032-52e8-4ec0-b916-3bc4b49b956c', FALSE),
       (1, 'TRK003', 'cddf1383-6d8e-4f55-a39f-8a71477317d3', FALSE);

INSERT INTO trailer (version, register_plate_number, business_id, in_use, cross_hitch)
VALUES (1, 'TRL001', '542602cf-97d5-4548-8831-55f21d35fcf4', FALSE, FALSE),
       (1, 'TRL002', 'a42c8aab-a60f-4a77-991e-d97c6248b33f', FALSE, FALSE),
       (1, 'TRL003', '5d41877e-a0c4-45e0-9b85-2de15e9352ee', TRUE, FALSE);

INSERT INTO trailer (version, register_plate_number, business_id, in_use, cross_hitch, start_period_date,
                     end_period_date)
VALUES (1, 'ABC1234', 'e8a2f7c1-8b1d-4bfb-9d92-3a1b7c6e5071', TRUE, FALSE, '2024-06-01 08:00:00', '2024-06-15 08:00:00'),
       (1, 'XYZ5678', 'f47b8c5a-b6d4-4e51-b4b9-a62bfa7d8f3c', FALSE, TRUE, '2024-06-05 09:00:00', '2024-06-19 09:00:00'),
       (1, 'LMN9101', '21e5d687-3b69-462a-a4b4-ff7a4d8e9a24', TRUE, TRUE, '2024-06-10 07:00:00', '2024-06-24 07:00:00'),
       (1, 'OPQ2345', '9cdbb6b1-7a47-4f37-9c5e-1c1f98cbb6eb', FALSE, FALSE, '2024-06-15 10:00:00', '2024-06-29 10:00:00'),
       (1, 'RST6789', 'd2e9c908-88b5-4c82-9d29-cbcbf6a4e1a9', TRUE, TRUE, '2024-06-20 06:00:00', '2024-07-04 06:00:00');

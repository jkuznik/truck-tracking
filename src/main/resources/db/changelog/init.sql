CREATE TABLE IF NOT EXISTS truck (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     truckPlateNumber VARCHAR(255) NOT NULL UNIQUE,
    inUse BOOLEAN,
    startPeriod TIMESTAMP,
    endPeriod TIMESTAMP,
    length DOUBLE NOT NULL,
    height DOUBLE NOT NULL,
    weight DOUBLE NOT NULL
    );

CREATE TABLE IF NOT EXISTS trailer (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       trailerPlateNumber VARCHAR(255) NOT NULL UNIQUE,
    inUse BOOLEAN,
    crossHitch BOOLEAN,
    startPeriod TIMESTAMP,
    endPeriod TIMESTAMP,
    length DOUBLE NOT NULL,
    height DOUBLE NOT NULL,
    weight DOUBLE NOT NULL
    );

CREATE TABLE IF NOT EXISTS truck_trailer (
                                             truck_id BIGINT,
                                             trailer_id BIGINT,
                                             PRIMARY KEY (truck_id, trailer_id),
    FOREIGN KEY (truck_id) REFERENCES truck(id),
    FOREIGN KEY (trailer_id) REFERENCES trailer(id)
    );

INSERT INTO truck (truckPlateNumber, inUse, length, height, weight)
VALUES
    ('TRK001', FALSE, 7.5, 3.2, 15000.0),
    ('TRK002', FALSE, 6.0, 2.8, 12000.0),
    ('TRK003', FALSE, 8.0, 3.5, 18000.0);

INSERT INTO trailer (trailerPlateNumber, inUse, crossHitch, length, height, weight)
VALUES
    ('TRL001', FALSE, FALSE, 15.0, 4.0, 3500.0),
    ('TRL002', FALSE, FALSE, 12.5, 3.5, 3000.0),
    ('TRL003', FALSE, FALSE, 14.0, 3.8, 3200.0);

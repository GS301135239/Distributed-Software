CREATE TABLE Good(
    goodId BIGINT AUTO_INCREMENT PRIMARY KEY,
    goodName VARCHAR(255) NOT NULL,
    goodDescription VARCHAR(1000),
    goodPrice DECIMAL(10, 2) NOT NULL,
    goodInventory INT NOT NULL
)
-- Create the schema that we'll use to populate data and watch the effect in the binlog
CREATE SCHEMA devices;
SET search_path TO devices;

-- Create and populate our products using a single insert with many rows
CREATE TABLE deviceinfo (
  id VARCHAR(255) NOT NULL PRIMARY KEY,
  manufacturer VARCHAR(255) NOT NULL,
  owner VARCHAR(255) NOT NULL
);

INSERT INTO deviceinfo
VALUES ('1', 'manufacturer-A', 'user-1'),
        ('2', 'manufacturer-B', 'user-2'),
        ('3', 'espressif systems', 'paolo');
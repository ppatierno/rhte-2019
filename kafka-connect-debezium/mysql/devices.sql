
# In production you would almost certainly limit the replication user must be on the follower (slave) machine,
# to prevent other clients accessing the log from other machines. For example, 'replicator'@'follower.acme.com'.
#
# However, this grant is equivalent to specifying *any* hosts, which makes this easier since the docker host
# is not easily known to the Docker container. But don't do this in production.
#
GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'replicator' IDENTIFIED BY 'replpass';
GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT  ON *.* TO 'debezium' IDENTIFIED BY 'dbz';

# Create the database that we'll use to populate data and watch the effect in the binlog
CREATE DATABASE devices;
GRANT ALL PRIVILEGES ON devices.* TO 'mysqluser'@'%';

# Switch to this database
USE devices;

# Create and populate our products using a single insert with many rows
CREATE TABLE deviceinfo (
  deviceid VARCHAR(255) NOT NULL PRIMARY KEY,
  manufacturer VARCHAR(255) NOT NULL
);

INSERT INTO deviceinfo
VALUES ("1", "manufacturer-A"),
        ("2", "manufacturer-B");
# Module 02 - CDC with Apache Kafka Connect and Debezium

## Deploy and check MySQL database

Deploy the MySQL database used as storage for devices information with some pre-crated data.

```shell
oc apply -f kafka-connect-debezium/mysql/mysql.yaml
```

Check that the table `deviceinfo` is prepopulated with some devices related information.

```shell
oc exec $(oc get pods --selector=app=mysql -o=jsonpath='{.items[0].metadata.name}') -- mysql -u mysqluser -pmysqlpw -e "SELECT * from deviceinfo" devices
```

## Deploy Apache Kafka Connect

The Apache Kafka Connect cluster can be deployed using the cluster operator.
There are two available CRDs (Custom Resource Definitions) for that: `KafkaConnect` and `KafkaConnectS2I`.
The `KafkaConnectS2I` CRD leverages the Source-2-Image OpenShift's feature for handling builds and adding connectors plugin.
It is used for adding the Debezium MySQL plugin connector.

First, deploy the Apache Kafka Connect cluster.

```shell
oc apply -f kafka-connect-debezium/kafka-connect-s2i.yaml
```

## Build a new Apache Kafka Connect image with MySQL Debezium connector plugin

Download the latest available Debezium MySQL connector plugin and start a new S2I build providing such a plugin.
A new Kafka Connect image is built adding the plugin and it's restarted.

```shell
export DEBEZIUM_VERSION=0.9.5.Final
mkdir -p plugins && cd plugins && \
curl http://central.maven.org/maven2/io/debezium/debezium-connector-mysql/$DEBEZIUM_VERSION/debezium-connector-mysql-$DEBEZIUM_VERSION-plugin.tar.gz | tar xz && \
oc start-build my-connect-cluster-connect --from-dir=. --follow && \
cd .. && rm -rf plugins
```

Check that the connector plugin is loaded successfully in the new image.
Interact againt the Apache Kafka Connect REST API (from one of the pods running in the OpenShift cluster).

```shell
oc exec my-cluster-kafka-0 -c kafka -- curl -s http://my-connect-cluster-connect-api:8083/connector-plugins
```

## Run the Debezium MySQL connector

Register the Debezium MySQL connector with the related configuration to run against the deployed MySQL instance:

```shell
oc exec -i -c kafka my-cluster-kafka-0 -- curl -X POST \
    -H "Accept:application/json" \
    -H "Content-Type:application/json" \
    http://my-connect-cluster-connect-api:8083/connectors -d @- <<'EOF'

{
    "name": "devices-connector",
    "config": {
        "connector.class": "io.debezium.connector.mysql.MySqlConnector",
        "tasks.max": "1",
        "database.hostname": "mysql",
        "database.port": "3306",
        "database.user": "debezium",
        "database.password": "dbz",
        "database.server.id": "184054",
        "database.server.name": "dbserver1",
        "database.whitelist": "devices",
        "database.history.kafka.bootstrap.servers": "my-cluster-kafka-bootstrap:9092",
        "database.history.kafka.topic": "schema-changes.devices"
    }
}
EOF
```

Check that the connector is now loaded.

```shell
oc exec my-cluster-kafka-0 -c kafka -- curl -X GET -H "Accept:application/json" http://my-connect-cluster-connect-api:8083/connectors
```

Check that the connector is running and it already read the propulated data in the `deviceinfo` table of the `devices` database from the MySQL instance, sending related events to the `dbserver1.devices.deviceinfo` topic.
Run an Apache Kafka console consumer on one of the pods for receiving messages from the topic from the beginning offset.

```shell
oc exec my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic dbserver1.devices.deviceinfo
```

## Making new CDC events

In another terminal, make a change to the `deviceinfo` table in the `devices` database of the MySQL instance, adding a new device.

```shell
oc exec $(oc get pods --selector=app=mysql -o=jsonpath='{.items[0].metadata.name}') -- mysql -u mysqluser -pmysqlpw -e "INSERT INTO deviceinfo VALUES(\"3\",\"manufacturer-C\")" devices
```

A new event is genareted by the Debezium MySQL connector and the consumer gets the message.

Delete the just created record from the table.
The connector generates a new message with `null` as payload which represents the tombstone for deleted record so deleting message with same key in a compacted topic.

```shell
oc exec $(oc get pods --selector=app=mysql -o=jsonpath='{.items[0].metadata.name}') -- mysql -u mysqluser -pmysqlpw -e "DELETE FROM deviceinfo WHERE id=\"3\"" devices
```
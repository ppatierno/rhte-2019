# Module 02 - CDC with Apache Kafka Connect and Debezium

## Deploy and check PostgreSQL database

Deploy the PostgreSQL database used as storage for devices information with some pre-crated data.

```shell
oc adm policy add-scc-to-user anyuid -z postgres
oc apply -f kafka-connect-debezium/postgres/postgres.yaml
```

Check that the table `deviceinfo` is prepopulated with some devices related information.

```shell
oc exec $(oc get pods --selector=app=postgres -o=jsonpath='{.items[0].metadata.name}') -- env PGOPTIONS="--search_path=devices" psql -U postgres -c "SELECT * FROM deviceinfo;"
```

## Deploy Apache Kafka Connect

The Apache Kafka Connect cluster can be deployed using the cluster operator.
There are two available CRDs (Custom Resource Definitions) for that: `KafkaConnect` and `KafkaConnectS2I`.
The `KafkaConnect` CRD uses Kafka Connect image as a base layer and the connector plugins are injected by building new image on top of it.
The `KafkaConnectS2I` CRD leverages the Source-2-Image OpenShift's feature for handling builds and adding connectors plugin.
In this workshop we will use `KafkaConnect` for adding the Debezium PostgreSQL plugin connector.
If you want to use `KafkaConnectS2I`, you can follow [KafkaConnectS2I module](02-cdc-connect-s2i-debezium.md). 

First, we will need to build a image containing desired plugins.
NOTE: You can skip building image and use prepared one from the `quay.io/amqstreamsrhte2019/rhte-kafka-connect-debezium-postgres:latest`

To download Debezium PostgreSQL connector plugin, build and push image to `your_repository` you can use a command:
```shell
export DEBEZIUM_VERSION=0.9.5.Final
mkdir -p kafka-connect-debezium/my-plugins && cd kafka-connect-debezium/my-plugins && \
curl http://central.maven.org/maven2/io/debezium/debezium-connector-postgres/$DEBEZIUM_VERSION/debezium-connector-postgres-$DEBEZIUM_VERSION-plugin.tar.gz | tar xz && cd .. && \
docker build -t your_repository/rhte-kafka-connect-debezium-postgres:latest . && \
docker push your_repository/rhte-kafka-connect-debezium-postgres:latest  && \
rm -rf my-plugins && cd ..
```

You can deploy the Kafka Connect cluster with the built image now.

```shell
oc apply -f kafka-connect-debezium/kafka-connect.yaml
```
NOTE: If you build your own image, you have to replace it in the `spec.image` of `kafka-connect-debezium/kafka-connect.yaml` file.
You can use this command:
```shell
sed -i 's/image: .*/image: your_repository\/rhte-kafka-connect-debezium-postgres:latest/' kafka-connect-debezium/kafka-connect.yaml
```

Check that the connector plugin is loaded successfully in the new image.
Interact against the Apache Kafka Connect REST API (from one of the pods running in the OpenShift cluster).

```shell
oc exec my-cluster-kafka-0 -c kafka -- curl -s http://my-connect-cluster-connect-api:8083/connector-plugins
```

## Run the Debezium PostgreSQL connector

Register the Debezium PostgreSQL connector with the related configuration to run against the deployed PostgreSQL instance:

```shell
oc exec -i -c kafka my-cluster-kafka-0 -- curl -X POST \
    -H "Accept:application/json" \
    -H "Content-Type:application/json" \
    http://my-connect-cluster-connect-api:8083/connectors -d @- <<'EOF'

{
  "name": "devices-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "postgres",
    "database.password": "postgres",
    "database.dbname" : "postgres",
    "database.server.name": "dbserver1",
    "table.whitelist": "devices.deviceinfo"
  }
}
EOF
```

Check that the connector is now loaded.

```shell
oc exec my-cluster-kafka-0 -c kafka -- curl -X GET -H "Accept:application/json" http://my-connect-cluster-connect-api:8083/connectors
```

Check that the connector is running and it already read the propulated data in the `deviceinfo` table of the `devices` database from the PostgreSQL instance, sending related events to the `dbserver1.devices.deviceinfo` topic.
Run an Apache Kafka console consumer on one of the pods for receiving messages from the topic from the beginning offset.

```shell
export CONSOLE_CONSUMER_PASSWORD=$(oc get secret kafka-console-consumer -o jsonpath='{.data.password}' | base64 -d)
oc exec -it my-cluster-kafka-0 -c kafka -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server my-cluster-kafka-bootstrap:9092 \
    --from-beginning \
    --property print.key=true \
    --topic dbserver1.devices.deviceinfo \
    --consumer-property sasl.mechanism=SCRAM-SHA-512 \
    --consumer-property security.protocol=SASL_PLAINTEXT \
    --consumer-property sasl.jaas.config="org.apache.kafka.common.security.scram.ScramLoginModule required username=\"kafka-console-consumer\" password=\"${CONSOLE_CONSUMER_PASSWORD}\";" \
    --group kafka-console-consumer
```

## Making new CDC events

In another terminal, make a change to the `deviceinfo` table in the `devices` database of the PostgreSQL instance, adding a new device.

```shell
oc exec $(oc get pods --selector=app=postgres -o=jsonpath='{.items[0].metadata.name}') -- env PGOPTIONS="--search_path=devices" psql -U postgres -c "INSERT INTO deviceinfo VALUES('4', 'manufacturer-C')"
```

A new event is genareted by the Debezium PostgreSQL connector and the consumer gets the message.

Delete the just created record from the table.
The connector generates a new message with `null` as payload which represents the tombstone for deleted record so deleting message with same key in a compacted topic.

```shell
oc exec $(oc get pods --selector=app=postgres -o=jsonpath='{.items[0].metadata.name}') -- env PGOPTIONS="--search_path=devices" psql -U postgres -c "DELETE FROM deviceinfo WHERE id='4';"
```

[Previous - Running Apache Kafka on OpenShift](01-kafka-on-openshift.md) | [Next - Data stream processing with Apache Kafka Stream API](03-streams-api.md)
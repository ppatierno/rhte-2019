# Module 04 - Bridging HTTP to Apache Kafka

## Deploying the HTTP-Kafka bridge

The HTTP-Kafka bridge provides a way for bridging HTTP/1.1 protocol to the native Apache Kafka protocol.

Deploy the bridge by running the following command.

```shell
oc apply -f kafka-http-bridge/kafka-bridge.yaml
```

## Deploying HTTP devices simulator

In order to simulate telemetry data traffic to ingest in the pipeline, a HTTP device simulator applicaton is used.
It is based on Vert.x and allows to simulate a specified number of devices (through the `DEVICE_IDS` list env var) sending random telemetry data.

Deploy the device application by running the following command.

```shell
oc apply -f http-device/http-device.yaml
```

The application will simulate sending telemetry data enriched with related devices information by the Kafka Streams enricher application.
The enriched data are written to the `device-telemetry-enriched` topic.

Check that the messages are coming to that topic.

```shell
export CONSOLE_CONSUMER_PASSWORD=$(oc get secret kafka-console-consumer -o jsonpath='{.data.password}' | base64 -d)
oc exec my-cluster-kafka-0 -c kafka -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server my-cluster-kafka-bootstrap:9092 \
    --from-beginning \
    --property print.key=true \
    --topic device-telemetry-enriched \
    --consumer-property sasl.mechanism=SCRAM-SHA-512 \
    --consumer-property security.protocol=SASL_PLAINTEXT \
    --consumer-property sasl.jaas.config="org.apache.kafka.common.security.scram.ScramLoginModule required username=\"kafka-console-consumer\" password=\"${CONSOLE_CONSUMER_PASSWORD}\";" \
    --group kafka-console-consumer
```

## CDC in action: update device information

In order to see the Debezium CDC feature in place, try to change a device information.
For example, you can change the "owner" of one of the devices running the following query.

```shell
oc exec $(oc get pods --selector=app=postgres -o=jsonpath='{.items[0].metadata.name}') -- env PGOPTIONS="--search_path=devices" psql -U postgres -c "UPDATE deviceInfo SET owner='new-user' WHERE id='1'"
```

After updating the row on the PostgreSQL database table, the related Debezium connector generates a new "update" event which is joined in real time with the data coming from the device.
You should see that the enriched data emitted by the Kafka Streams application are now in real time updated with the new device info.

## Using real ESP8266 device

Other than using the previous HTTP devices simulator, it's possible to use a real device for sending the data connecting from outside the cluster.
As an example, you can use an [ESP8266](https://www.espressif.com/en/products/hardware/esp8266ex/overview) device and the related application provided in this [repo](https://github.com/ppatierno/esp8266-http-device).

[Previous - Data stream processing with Apache Kafka Stream API](03-streams-api.md) | [Next - InfluxDB and telemetry data Grafana dashbaord](appendix-influxdb.md)
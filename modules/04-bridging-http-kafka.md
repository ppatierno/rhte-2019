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

## Using real ESP8266 device

Other than using the previous HTTP devices simulator, it's possible to use a real device for sending the data connecting from outside the cluster.
As an example, you can use an [ESP8266](https://www.espressif.com/en/products/hardware/esp8266ex/overview) device and the related application provided in this [repo](https://github.com/ppatierno/esp8266-http-device).

[Previous - Data stream processing with Apache Kafka Stream API](03-streams-api.md) | [Next - InfluxDB and telemetry data Grafana dashbaord](appendix-influxdb.md)
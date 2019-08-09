# Module 04 - Bridging HTTP to Apache Kafka

## Deploying the HTTP-Kafka bridge

The HTTP-Kafka bridge provides a way for bridging HTTP/1.1 protocol to the native Apache Kafka protocol.

Deploy the bridge by running the following command.

```shell
oc apply -f kafka/kafka-bridge.yaml
```

## Deploying HTTP devices simulator

In order to simulate telemetry data traffic to ingest in the pipeline, a HTTP device simulator applicaton is used.
It is based on Vert.x and allows to simulate a specified number of devices (through the `DEVICE_IDS` list env var) sending random telemetry data.

Deploy the device application by running the following command.

```shell
oc apply -f http-device/http-device.yaml
```

The application will emulate devices seinding telemetry data enriched with related devices information by the KStreams enricher application.
The enriched data are written to the `device-telemetry-enriched` topic.

Check that the messages are coming to that topic.

```shell
export CONSOLE_CONSUMER_PASSWORD=$(oc get secret kafka-console-consumer -o jsonpath='{.data.password}' | base64 -d)
oc exec my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server my-cluster-kafka-bootstrap:9092 \
    --from-beginning \
    --property print.key=true \
    --topic device-telemetry-enriched \
    --consumer-property sasl.mechanism=SCRAM-SHA-512 \
    --consumer-property security.protocol=SASL_PLAINTEXT \
    --consumer-property sasl.jaas.config="org.apache.kafka.common.security.scram.ScramLoginModule required username=\"kafka-console-consumer\" password=\"${CONSOLE_CONSUMER_PASSWORD}\";" \
    --group kafka-console-consumer
```
# Module 04 - Bridging HTTP to Apache Kafka

## Deploying the HTTP-Kafka bridge

The HTTP-Kafka bridge provides a way for bridging HTTP/1.1 protocol to the native Apache Kafka protocol.

Deploy the operator by running the following command.

```shell
oc apply -f kafka/kafka-bridge.yaml
```

## Deploying HTTP devices simulator

In order to simulate telemetry data traffic to ingest in the pipeline, a HTTP device simulator applicaton is used.
It is based on Vert.x and allow to simulate a specified number of devices (thruogh the `DEVICE_IDS` list env var) sending random telemetry data.

Deploy the application by running the following command.

```shell
oc apply -f http-device/http-device.yaml
```
# Module 03 - Data stream processing with Apache Kafka Stream API

## Deploy the Kafka Streams enricher application

Deploy the Kafka Streams enricher application.

```shell
oc apply -f kstreams-enricher/kstreams-enricher.yaml
```

In order to simulate some data and checking that the Kafka Streams application is working fine, joining this data with the device information coming from the CDC, use a Kafka console producer running on one of the pods on the cluster.

```shell
oc exec my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-producer.sh \
    --broker-list localhost:9092 \
    --topic device-telemetry \
    --property "parse.key=true" \
    --property "key.separator=:"
```

Send a few messages to the telemetry topic and check the log that the Kafka Streams application is joining with information data and enriching.

```shell
1:{ "deviceId": "1", "temperature": 15 }
2:{ "deviceId": "2", "temperature": 20 }
```
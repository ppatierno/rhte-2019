# Module 03 - Data stream processing with Apache Kafka Stream API

## Deploy the Kafka Streams enricher application

Deploy the Kafka Streams enricher application.

```shell
oc apply -f kstreams-enricher/kstreams-enricher.yaml
```

In order to simulate some data and checking that the Kafka Streams application is working fine, joining this data with the device information coming from the CDC, use a Kafka console producer running on one of the pods on the cluster.

```shell
export CONSOLE_PRODUCER_PASSWORD=$(oc get secret kafka-console-producer -o jsonpath='{.data.password}' | base64 -d)
oc exec -it my-cluster-kafka-0 -c kafka -- /opt/kafka/bin/kafka-console-producer.sh \
    --broker-list my-cluster-kafka-bootstrap:9092 \
    --topic device-telemetry \
    --property "parse.key=true" \
    --property "key.separator=:" \
    --producer-property sasl.mechanism=SCRAM-SHA-512 \
    --producer-property security.protocol=SASL_PLAINTEXT \
    --producer-property sasl.jaas.config="org.apache.kafka.common.security.scram.ScramLoginModule required username=\"kafka-console-producer\" password=\"${CONSOLE_PRODUCER_PASSWORD}\";"
```

Send a few messages to the telemetry topic and check the log that the Kafka Streams application is joining with information data and enriching.

```shell
1:{ "deviceId": "1", "temperature": 15, "humidity": 30 }
2:{ "deviceId": "2", "temperature": 20, "humidity": 35 }
```

[Previous - CDC with Apache Kafka Connect and Debezium](02-cdc-connect-debezium.md) | [Next - Bridging HTTP to Apache Kafka](04-bridging-http-kafka.md)
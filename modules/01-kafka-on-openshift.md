# Module 01 - Running Apache Kafka on OpenShift

## Apache Kafka cluster deployment

TBD

## Topics creation

The scenario needs three topics to work.

* `dbserver1.devices.deviceinfo`: the topic where the Debezium MySQL connector sends CDC events related to changes in the MySQL database table containing the devices information. The topic name is made by the `database.server.name` (one of the parameters of the connector configuration) which is `dbserver1`, the database name which is `devices` and the name of the table which is `deviceinfo`.
* `device-telemetry`: the topic where the data coming from the devices are stored.
* `device-telemetry-enriched`: the topic where the Kafka Streams application writes telemetry data enriched with the devices information.

The above topics are described by corresponding `KafkaTopic` resources in the `kafka/kafka-topics.yaml` file.
When these resources are created, the Topic Operator takes care of them in ordert to create the actual corresponding topics in the Apache Kafka cluster.

```shell
oc apply -f kafka/kafka-topics.yaml
```

## Users creation

The scenario is made by mainly three applications interacting with the Apache Kafka cluster in order to read/write from/to topics.
In order to improve the security, setting up authentication and authorization is important in order to allow only these applications to access the related topics with the needed rights for reading/writing.

* the Apache Kafka Connect cluster, using the Debezium MySQL connector, has to WRITE to the `dbserver1.devices.deviceinfo` topic.
* the Kafka Streams application has to READ from the `dbserver1.devices.deviceinfo` and `device-telemetry` topics and WRITE to the `device-telemetry-enriched` topic.
* the HTTP - Kafka bridge has to WRITE to the `device-telemetry` topic.

For each of these applications, an Apache Kafka user is created via a corresponding `KafkaUser` resource.
Within such a resource it is possible to describe the kind of authentication and authorization mechanism to use.
Finally, the ACLs (Access Control List) rules descibe the rights that the user has to read/write from/to topics.

The `KafkaUser` resources are described in the `kafka/kafka-users.yaml` file.

```shell
oc apply -f kafka/kafka-users.yaml
```
# Module 01 - Running Apache Kafka on OpenShift

## Cluster Operator deployment

Deploying an Apache Kafka cluster is made really simple using the Cluster Operator.
The Cluster Operator can be installed in many different ways:

* Using the Operator Hub with OpenShift 4.x which provides the OLM (Operator Lifecycle Manager) to simplify the operators installation.
* Installing the OLM first (on OpenShift 3.11) and then the Cluster Operator. If using Strimzi, it's possible through the [OperatorHub.io](https://operatorhub.io/) website. The user installing the OLM needs admin right to do so.
* Just downloading the released files and installing manually all the CRDs and the Cluster Operator (in this case the user doing that has to have admin rights).

### Operator Hub on OpenShift 4.x

TBD

### OperatorHub.io website

The OperatorHub.io website provides the latest version of the Strimzi project providing the Cluster Operator.
Because the process involves the OLM installation, it's useful for OpenShift 3.11 where the Operator Hub is not already available.
First of all, install the OLM (the user has to have adming rights to do so).

```shell
curl -sL https://github.com/operator-framework/operator-lifecycle-manager/releases/download/0.10.0/install.sh | bash -s 0.10.0
```

The OLM will be installed and running in the `olm` namespace.

After that, install the Cluster Operator running the following command.

```shell
oc apply -f https://operatorhub.io/install/strimzi-kafka-operator.yaml
```

The operator will be installed in the `operators` namespace and will be able to watch for `Kafka` resources in all the namespaces of the cluster.

### Manual installation

Download the latest release of the Strimzi project.
From the extracted folder, run the following command (with a user having admin rights).

```shell
oc apply -f install/cluster-operator
```

The operator will be running in the working namespace.

## Apache Kafka cluster deployment

The Cluster Operator is able to watch `Kafka` resources describing an Apache Kafka cluster.
In order to deploy the Apache Kafka cluster, just create the `Kafka` resource provided with the following command.

```shell
oc apply -f kafka/kafka-cluster.yaml
```

The main points on this cluser are:

* The Kafka cluster is made by 3 brokers (see `spec.kafka.replicas`)
* There are two listeners, a "plain" (`spec.kafka.listeners.plain`) one listening on port 9092 and "tls" (`spec.kafka.listeners.tls`) one on port 9093
* Authentication is enabled on both "plain" and "tls". It uses SCRAM-SHA-512 mechanism.
* Authorization is enabled on the Kafka cluster. It uses the "simple" mechanism (see `spec.kafka.authorization`)
* The Kafka brokers have some common configuration via `spec.kafka.config`
* The Kafka brokers uses JBOD for persistent storage with one disk (see `spec.kafka.storage`)
* The Kafka brokers export metrics through JMX exporter configured via `spec.kafka.metrics`
* The Zookeeper ensamble is made by 3 nodes (see `spec.zookeeper.replicas`)
* The Zookeeper ensamble uses persistent storage as well (see `spec.zookeeper.storage`)
* The Zookeeper nodes export metrics through JMX exporter configured via `spec.zookeeper.metrics`

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
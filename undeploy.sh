#!/bin/bash

NAMESPACE=${KAFKA_NAMESPACE:-rhte-demo}
CLUSTER=${KAFKA_CLUSTER:-rhte}
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

# delete Kafka users and topics
oc delete kafkauser --all -n $NAMESPACE
oc delete kafkatopic --all -n $NAMESPACE

# delete Kafka cluster
oc delete kafka $CLUSTER -n $NAMESPACE
oc delete pvc --all -n $NAMESPACE

# delete Prometheus operator stuff
oc delete deployment streams-prometheus-operator -n $NAMESPACE
oc delete service streams-prometheus-operator -n $NAMESPACE
oc delete clusterrolebinding streams-prometheus-operator -n $NAMESPACE
oc delete clusterrole streams-prometheus-operator -n $NAMESPACE
oc delete serviceaccount streams-prometheus-operator -n $NAMESPACE

oc delete secret additional-scrape-configs -n $NAMESPACE
oc delete secret alertmanager-alertmanager -n $NAMESPACE

oc delete alertmanager --all -n $NAMESPACE
oc delete prometheus --all -n $NAMESPACE
# TODO check what other kind of resource, the Prometheus operator creates that we need to delete here

# delete Grafana
oc delete deployment grafana -n $NAMESPACE
oc delete route grafana -n $NAMESPACE
oc delete service grafana -n $NAMESPACE

# delete Postgres related stuff
oc delete deployment postgres -n $NAMESPACE
oc delete service postgres -n $NAMESPACE
oc delete serviceaccount postgres -n $NAMESPACE

# delete Kafka Connect
oc delete kafkaconnect --all -n $NAMESPACE

# delete Kafka Streams application
oc delete deployment kstreams-enricher -n $NAMESPACE

# delete Kafka Bridge
oc delete kafkabridge my-bridge -n $NAMESPACE
oc delete route my-bridge-route -n $NAMESPACE

# delete simulated HTTP devices
oc delete deployment http-device -n $NAMESPACE

# delete InfluDB stuff
oc delete deployment influxdb -n $NAMESPACE
oc delete service influxdb -n $NAMESPACE

# delete Apache Camel Kafka - InfluxDB application
oc delete deployment camel-kafka-influxdb -n $NAMESPACE

# delete namespace itself
oc delete namespace $NAMESPACE

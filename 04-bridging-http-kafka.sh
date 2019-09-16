#!/bin/bash

NAMESPACE=${KAFKA_NAMESPACE:-rhte-demo}
CLUSTER=${KAFKA_CLUSTER:-rhte}
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

# deploy the Kafka Bridge
sed "s/my-cluster/$CLUSTER/" $DIR/kafka-http-bridge/kafka-bridge.yaml > $DIR/kafka-http-bridge/$CLUSTER-kafka-bridge.yaml

oc apply -f $DIR/kafka-http-bridge/$CLUSTER-kafka-bridge.yaml -n $NAMESPACE

echo "Waiting for Kafka Bridge to be ready..."
oc rollout status deployment/my-bridge-bridge -w -n $NAMESPACE
echo "...Kafka Bridge ready"

rm $DIR/kafka-http-bridge/$CLUSTER-kafka-bridge.yaml

# deploy the simulated HTTP devices
oc apply -f $DIR/http-device/http-device.yaml

echo "Waiting for HTTP devices to be ready..."
oc rollout status deployment/http-device -w -n $NAMESPACE
echo "...HTTP devices ready"
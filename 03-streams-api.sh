#!/bin/bash

NAMESPACE=${KAFKA_NAMESPACE:-rhte-demo}
CLUSTER=${KAFKA_CLUSTER:-rhte}
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

sed "s/my-cluster/$CLUSTER/" $DIR/kstreams-enricher/kstreams-enricher.yaml > $DIR/kstreams-enricher/$CLUSTER-kstreams-enricher.yaml

oc apply -f $DIR/kstreams-enricher/$CLUSTER-kstreams-enricher.yaml -n $NAMESPACE

echo "Waiting for Kafka Streams application to be ready..."
oc rollout status deployment/kstreams-enricher -w -n $NAMESPACE
echo "...Kafka Streams ready"

rm $DIR/kstreams-enricher/$CLUSTER-kstreams-enricher.yaml
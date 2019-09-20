#!/bin/bash

NAMESPACE=${KAFKA_NAMESPACE:-rhte-demo}
CLUSTER=${KAFKA_CLUSTER:-rhte}
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

# ensure to be on the right namespace
oc project $KAFKA_NAMESPACE 2> /dev/null || oc new-project $KAFKA_NAMESPACE

# deploy Kafka Streams application
sed "s/my-cluster/$CLUSTER/" $DIR/kstreams-enricher/kstreams-enricher.yaml > $DIR/kstreams-enricher/$CLUSTER-kstreams-enricher.yaml

oc apply -f $DIR/kstreams-enricher/$CLUSTER-kstreams-enricher.yaml -n $NAMESPACE

echo "Waiting for Kafka Streams application to be ready..."
oc rollout status deployment/kstreams-enricher -w -n $NAMESPACE
echo "...Kafka Streams application ready"

rm $DIR/kstreams-enricher/$CLUSTER-kstreams-enricher.yaml
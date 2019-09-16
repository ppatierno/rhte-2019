#!/bin/bash

echo "KAFKA_NAMESPACE=" $KAFKA_NAMESPACE
echo "KAFKA_CLUSTER=" $KAFKA_CLUSTER

oc project $KAFKA_NAMESPACE 2> /dev/null || oc new-project $KAFKA_NAMESPACE

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

$DIR/01-kafka-on-openshift.sh
$DIR/02-cdc-connect-debezium.sh
$DIR/03-streams-api.sh
$DIR/04-bridging-http-kafka.sh
$DIR/appendix-influxdb.sh
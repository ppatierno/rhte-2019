#!/bin/bash

NAMESPACE=${KAFKA_NAMESPACE:-rhte-demo}
CLUSTER=${KAFKA_CLUSTER:-rhte}
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

# deploy the InfluxDB
oc apply -f $DIR/camel-kafka-influxdb/influxdb.yaml

echo "Waiting for InfluxDB to be ready..."
oc rollout status deployment/influxdb -w -n $NAMESPACE
echo "...InfluxDB ready"

# deploy the Camel Kafka - InfluxDB application
sed "s/my-cluster/$CLUSTER/" $DIR/camel-kafka-influxdb/camel-kafka-influxdb.yaml > $DIR/camel-kafka-influxdb/$CLUSTER-camel-kafka-influxdb.yaml

oc apply -f $DIR/camel-kafka-influxdb/$CLUSTER-camel-kafka-influxdb.yaml

echo "Waiting for Apache Camel Kafka - InfluxDB application to be ready..."
oc rollout status deployment/camel-kafka-influxdb -w -n $NAMESPACE
echo "...Apache Camel Kafka - InfluxDB application ready"

rm $DIR/camel-kafka-influxdb/$CLUSTER-camel-kafka-influxdb.yaml

curl -X POST http://admin:admin@$(oc get routes grafana -o jsonpath='{.status.ingress[0].host}{"\n"}')/api/datasources  -H 'Content-Type: application/json;charset=UTF-8' --data-binary '{"name":"InfluxDB","isDefault":true ,"type":"influxdb","url":"http://influxdb:8086","access":"proxy","basicAuth":false,"database":"sensor"}'
curl -X POST http://admin:admin@$(oc get routes grafana -o jsonpath='{.status.ingress[0].host}{"\n"}')/api/dashboards/import -d @$DIR/metrics/grafana/kafka-iot-dashboard.json --header "Content-Type: application/json"
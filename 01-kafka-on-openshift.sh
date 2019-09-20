#!/bin/bash

NAMESPACE=${KAFKA_NAMESPACE:-rhte-demo}
CLUSTER=${KAFKA_CLUSTER:-rhte}
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

# ensure to be on the right namespace
oc project $KAFKA_NAMESPACE 2> /dev/null || oc new-project $KAFKA_NAMESPACE

sed "s/my-cluster/$CLUSTER/" $DIR/kafka/kafka-cluster.yaml > $DIR/kafka/$CLUSTER-kafka-cluster.yaml

oc apply -f $DIR/kafka/$CLUSTER-kafka-cluster.yaml -n $NAMESPACE

# delay for allowing cluster operator to create the Zookeeper statefulset
sleep 5

zkReplicas=$(oc get kafka $CLUSTER -o jsonpath="{.spec.zookeeper.replicas}" -n $NAMESPACE)
echo "Waiting for Zookeeper cluster to be ready..."
readyReplicas="0"
while [ "$readyReplicas" != "$zkReplicas" ]
do
    sleep 2
    readyReplicas=$(oc get statefulsets $CLUSTER-zookeeper -o jsonpath="{.status.readyReplicas}" -n $NAMESPACE)
done
echo "...Zookeeper cluster ready"

# delay for allowing cluster operator to create the Kafka statefulset
sleep 5

kReplicas=$(oc get kafka $CLUSTER -o jsonpath="{.spec.kafka.replicas}" -n $NAMESPACE)
echo "Waiting for Kafka cluster to be ready..."
readyReplicas="0"
while [ "$readyReplicas" != "$kReplicas" ]
do
    sleep 2
    readyReplicas=$(oc get statefulsets $CLUSTER-kafka -o jsonpath="{.status.readyReplicas}" -n $NAMESPACE)
done
echo "...Kafka cluster ready"

echo "Waiting for entity operator to be ready..."
oc rollout status deployment/$CLUSTER-entity-operator -w -n $NAMESPACE
echo "...entity operator ready"

rm $DIR/kafka/$CLUSTER-kafka-cluster.yaml

# create Kafka topics
sed "s/my-cluster/$CLUSTER/" $DIR/kafka/kafka-topics.yaml > $DIR/kafka/$CLUSTER-kafka-topics.yaml
oc apply -f $DIR/kafka/$CLUSTER-kafka-topics.yaml -n $NAMESPACE
rm $DIR/kafka/$CLUSTER-kafka-topics.yaml

# create Kafka users
sed "s/my-cluster/$CLUSTER/" $DIR/kafka/kafka-users.yaml > $DIR/kafka/$CLUSTER-kafka-users.yaml
oc apply -f $DIR/kafka/$CLUSTER-kafka-users.yaml -n $NAMESPACE
rm $DIR/kafka/$CLUSTER-kafka-users.yaml

# deploy Prometheus operator
sed "s/namespace: .*/namespace: $NAMESPACE/" $DIR/metrics/prometheus-operator.yaml > $DIR/metrics/prometheus-operator-deploy.yaml

oc apply -f $DIR/metrics/prometheus-operator-deploy.yaml -n $NAMESPACE

echo "Waiting for Prometheus operator to be ready..."
oc rollout status deployment/streams-prometheus-operator -w -n $NAMESPACE
echo "...Prometheus operator ready"

rm $DIR/metrics/prometheus-operator-deploy.yaml

oc create secret generic additional-scrape-configs --from-file=$DIR/metrics/prometheus/additional-properties/prometheus-additional.yaml
oc create secret generic alertmanager-alertmanager --from-file=alertmanager.yaml=$DIR/metrics/prometheus/alertmanager-config/alert-manager-config.yaml

sed "s/namespace: .*/namespace: $NAMESPACE/" $DIR/metrics/prometheus/install/prometheus.yaml > $DIR/metrics/prometheus/install/prometheus-deploy.yaml

oc apply -f $DIR/metrics/prometheus/install/prometheus-rules.yaml -n $NAMESPACE
oc apply -f $DIR/metrics/prometheus/install/prometheus-deploy.yaml -n $NAMESPACE
rm $DIR/metrics/prometheus/install/prometheus-deploy.yaml
oc apply -f $DIR/metrics/prometheus/install/strimzi-service-monitor.yaml -n $NAMESPACE
oc apply -f $DIR/metrics/prometheus/install/alert-manager.yaml -n $NAMESPACE

# delay for allowing Prometheus operator to create the Alert manager statefulset
sleep 5

amReplicas=$(oc get statefulsets alertmanager-alertmanager -o jsonpath="{.spec.replicas}" -n $NAMESPACE)
echo "Waiting for Alert manager to be ready..."
readyReplicas="0"
while [ "$readyReplicas" != "$amReplicas" ]
do
    sleep 2
    readyReplicas=$(oc get statefulsets alertmanager-alertmanager -o jsonpath="{.status.readyReplicas}" -n $NAMESPACE)
done
echo "...Alert manager ready"

# delay for allowing Prometheus operator to create the Prometheus statefulset
sleep 5

pReplicas=$(oc get statefulsets prometheus-prometheus -o jsonpath="{.spec.replicas}" -n $NAMESPACE)
echo "Waiting for Prometheus to be ready..."
readyReplicas="0"
while [ "$readyReplicas" != "$pReplicas" ]
do
    sleep 2
    readyReplicas=$(oc get statefulsets prometheus-prometheus -o jsonpath="{.status.readyReplicas}" -n $NAMESPACE)
done
echo "...Prometheus ready"

# Grafana
oc apply -f $DIR/metrics/grafana/grafana.yaml -n $NAMESPACE
oc expose service/grafana -n $NAMESPACE

echo "Waiting for Grafana server to be ready..."
oc rollout status deployment/grafana -w -n $NAMESPACE
echo "...Grafana server ready"

sleep 5
# posting Prometheus datasource and dashboards to Grafana
curl -X POST http://admin:admin@$(oc get routes grafana -o jsonpath='{.status.ingress[0].host}{"\n"}')/api/datasources  -H 'Content-Type: application/json;charset=UTF-8' --data-binary '{"name":"Prometheus","isDefault":true ,"type":"prometheus","url":"http://prometheus:9090","access":"proxy","basicAuth":false}'
curl -X POST http://admin:admin@$(oc get routes grafana -o jsonpath='{.status.ingress[0].host}{"\n"}')/api/dashboards/import -d @$DIR/metrics/grafana/strimzi-kafka.json --header "Content-Type: application/json"
curl -X POST http://admin:admin@$(oc get routes grafana -o jsonpath='{.status.ingress[0].host}{"\n"}')/api/dashboards/import -d @$DIR/metrics/grafana/strimzi-zookeeper.json --header "Content-Type: application/json"
curl -X POST http://admin:admin@$(oc get routes grafana -o jsonpath='{.status.ingress[0].host}{"\n"}')/api/dashboards/import -d @$DIR/metrics/grafana/strimzi-kafka-connect.json --header "Content-Type: application/json"
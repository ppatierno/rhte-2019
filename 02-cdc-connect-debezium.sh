#!/bin/bash

NAMESPACE=${KAFKA_NAMESPACE:-rhte-demo}
CLUSTER=${KAFKA_CLUSTER:-rhte}
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

# ensure to be on the right namespace
oc project $NAMESPACE 2> /dev/null || oc new-project $NAMESPACE

# deploy PostgreSQL server
oc adm policy add-scc-to-user anyuid -z postgres
oc apply -f $DIR/kafka-connect-debezium/postgres/postgres.yaml

echo "Waiting for PostgreSQL server to be ready..."
oc rollout status deployment/postgres -w -n $NAMESPACE
echo "...PostgreSQL server ready"

# deploy Kafka Connect cluster
sed "s/my-cluster/$CLUSTER/" $DIR/kafka-connect-debezium/kafka-connect.yaml > $DIR/kafka-connect-debezium/$CLUSTER-kafka-connect.yaml
oc apply -f $DIR/kafka-connect-debezium/$CLUSTER-kafka-connect.yaml

echo "Waiting for Kafka Connect to be ready..."
oc rollout status deployment/my-connect-cluster-connect -w -n $NAMESPACE
echo "...Kafka Connect ready"

rm $DIR/kafka-connect-debezium/$CLUSTER-kafka-connect.yaml

oc exec -i -c kafka $CLUSTER-kafka-0 -- curl -s -X POST \
    -H "Accept:application/json" \
    -H "Content-Type:application/json" \
    http://my-connect-cluster-connect-api:8083/connectors -d @- <<'EOF'

{
  "name": "devices-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "postgres",
    "database.password": "postgres",
    "database.dbname" : "postgres",
    "database.server.name": "dbserver1",
    "table.whitelist": "devices.deviceinfo"
  }
}
EOF
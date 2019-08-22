# Appendix - InfluxDB and telemetry data Grafana dashbaord

After the enriched data are available in the `device-telemetry-enriched` topic, it should be possible to show them on a dashboard.
One of the possible approaches is using a time-series database, where the telemetry data are stored with a related timestamp and the showing them on a Grafana dashboard.
One of the well-known time-series databases is [InfluxDB](https://www.influxdata.com/).

## Setting up InfluxDB

The first step is about deploying an InfluxDB instance on the cluster by running the following command.

```shell
oc apply -f camel-kafka-influxdb/influxdb.yaml
```

## Running Apache Camel - Kafka - InfluxDB application

In order to read data from the `device-telemetry-enriched` topic and putting them into the InfluxDB `sensor` database, an Apache Camel based application is used.
This application just reads data from the topic and creates data points to store into the time-series database with related timestamp.
It can be deployed by running the following command.

```shell
oc apply -f camel-kafka-influxdb/camel-kafka-influxdb.yaml
```

To check that the incoming data are copied from the topic to the InfluxDB database, run the following SQL-like query on the database.

```shell
oc exec -it $(oc get pods --selector=app=influxdb -o=jsonpath='{.items[0].metadata.name}') -- influx -database 'sensor' -execute 'SELECT "deviceId", "temperature" FROM "device-data"'
```

## Setting up Grafana

The first step is about create a new datasource backed by the above InfluxDB instance.

```shell
curl -X POST http://admin:admin@$(oc get routes grafana -o jsonpath='{.status.ingress[0].host}{"\n"}')/api/datasources  -H 'Content-Type: application/json;charset=UTF-8' --data-binary '{"name":"InfluxDB","isDefault":true ,"type":"influxdb","url":"http://influxdb:8086","access":"proxy","basicAuth":false,"database":"sensor"}'
```

Than we can create a Grafana dashboard for showing the device telemetry data.

```shell
curl -X POST http://admin:admin@$(oc get routes grafana -o jsonpath='{.status.ingress[0].host}{"\n"}')/api/dashboards/import -d @metrics/grafana/kafka-iot-dashboard.json --header "Content-Type: application/json"
```
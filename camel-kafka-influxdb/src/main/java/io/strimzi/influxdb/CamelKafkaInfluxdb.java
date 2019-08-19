package io.strimzi.influxdb;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.util.concurrent.TimeUnit;

public class CamelKafkaInfluxdb {
    
    public static void main(String[] args) throws Exception {
        
        DataWriterConfig config = DataWriterConfig.fromMap(System.getenv());

        InfluxDB influxDB = InfluxDBFactory.connect(config.databaseUrl());
        influxDB.query(new Query("CREATE DATABASE " + config.database(), config.database()));

        SimpleRegistry registry = new SimpleRegistry();
        registry.put("connectionBean", influxDB);
        CamelContext camelContext = new DefaultCamelContext(registry);

        camelContext.addRoutes(new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                String kafkaConfig = "kafka:" + config.topicDeviceData() + "?brokers=" + config.bootstrapServers() + "&groupId=" + config.consumerGroup();


                if (config.username() != null && config.password() != null) {

                    String saslJaasConfig = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
                    String auth = "saslMechanism=SCRAM-SHA-512" +
                            "&securityProtocol=SASL_PLAINTEXT" +
                            "&saslJaasConfig=" + String.format(saslJaasConfig, config.username(), config.password());

                    kafkaConfig += "&" + auth;
                }

                from(kafkaConfig)
                .unmarshal().json(JsonLibrary.Jackson, DeviceData.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {

                        String deviceId = exchange.getIn().getHeader(KafkaConstants.KEY).toString();
                        DeviceData deviceData = (DeviceData) exchange.getIn().getBody();

                        Point point = Point.measurement(config.measurement())
                                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                                .tag("deviceId", deviceId)
                                .addField("temperature", deviceData.temperature)
                                .addField("manufacturer", deviceData.manufacturer)
                                .build();

                        exchange.getOut().setBody(point);
                    }
                })
                .to("influxdb://connectionBean?databaseName=" + config.database() + "&retentionPolicy=autogen")
                .routeId("kafka-influxdb-route")
                .log("${body}");
            }
        });

        camelContext.start();

        Thread.sleep(Long.MAX_VALUE);
    }
}

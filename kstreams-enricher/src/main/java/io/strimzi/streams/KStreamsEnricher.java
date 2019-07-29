package io.strimzi.streams;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.strimzi.streams.model.DeviceTelemetry;
import io.strimzi.streams.serde.JsonSerializer;
import io.strimzi.streams.serde.JsonDeserializer;

public final class KStreamsEnricher {

    private static final Logger log = LoggerFactory.getLogger(KStreamsEnricher.class);
    
    public static void main(String[] args) {

        KStreamsEnricherConfig config = KStreamsEnricherConfig.fromMap(System.getenv());
        log.info("Starting with config = {}", config);
        new KStreamsEnricher().run(config);
    }

    public void run(KStreamsEnricherConfig config) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, config.getApplicationId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());

        JsonSerializer<DeviceTelemetry> dJsonSerializer = new JsonSerializer<>();
        JsonDeserializer<DeviceTelemetry> dJsonDeserializer = new JsonDeserializer<>(DeviceTelemetry.class);
        Serde<DeviceTelemetry> deviceTelemetrySerdes = Serdes.serdeFrom(dJsonSerializer, dJsonDeserializer);
        
        StreamsBuilder builder = new StreamsBuilder();

        // TODO
        builder.stream("device-telemetry", Consumed.with(Serdes.String(), deviceTelemetrySerdes)).peek((deviceId, deviceTelemetry) -> {
            log.info("deviceId = {}, deviceTelemetry = {}", deviceId, deviceTelemetry);
        });

        KafkaStreams streams = new KafkaStreams(builder.build(), props);

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
    }
}
package io.strimzi.streams;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.strimzi.streams.model.DeviceInfo;
import io.strimzi.streams.model.DeviceTelemetry;
import io.strimzi.streams.serde.JsonSerializer;
import io.strimzi.streams.serde.ChangeEventAwareJsonSerde;
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
        
        Serde<DeviceTelemetry> deviceTelemetrySerdes = 
            Serdes.serdeFrom(new JsonSerializer<DeviceTelemetry>(), new JsonDeserializer<>(DeviceTelemetry.class));
        
        Serde<String> stringKeySerdes = new ChangeEventAwareJsonSerde<>(String.class);
        stringKeySerdes.configure(Collections.emptyMap(), true);

        Serde<DeviceInfo> deviceInfoSerdes = new ChangeEventAwareJsonSerde<>(DeviceInfo.class);
        deviceInfoSerdes.configure(Collections.emptyMap(), false);
        
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, DeviceTelemetry> deviceTelemetry = 
            builder.stream("device-telemetry", Consumed.with(Serdes.String(), deviceTelemetrySerdes));

        KTable<String, DeviceInfo> deviceInfo = 
            builder.table("dbserver1.devices.deviceinfo", Consumed.with(stringKeySerdes, deviceInfoSerdes));

        deviceTelemetry.join(deviceInfo, (telemetry, info) -> {
            log.info("info = {}, telemetry = {}", info, telemetry);
            // TODO enrichment
            return telemetry;
        }).to("device-telemetry-enriched", Produced.with(Serdes.String(), deviceTelemetrySerdes));
        
        KafkaStreams streams = new KafkaStreams(builder.build(), props);

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
    }
}
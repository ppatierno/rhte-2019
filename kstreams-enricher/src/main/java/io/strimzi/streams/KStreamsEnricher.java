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
        
        if (config.getUsername() != null && config.getPassword() != null) {
            props.put("sasl.mechanism","SCRAM-SHA-512");
            props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"" + config.getUsername() + "\" password=\"" + config.getPassword() + "\";");
            props.put("security.protocol","SASL_PLAINTEXT");
        }
        
        // Serdes for the device telemetry message key
        // messages coming through the bridge has key/value always encoded as JSON even if just bringing a String,
        // so for example it gets key = ""1"" instead of key = "1" so the need to deserialize via JSON
        Serde<String> deviceTelemetryKeySerdes =
            Serdes.serdeFrom(new JsonSerializer<String>(), new JsonDeserializer<>(String.class));

        // Serdes for the device telemetry message payload
        Serde<DeviceTelemetry> deviceTelemetrySerdes = 
            Serdes.serdeFrom(new JsonSerializer<DeviceTelemetry>(), new JsonDeserializer<>(DeviceTelemetry.class));
        
        // Serdes for the deviceinfo message key (the device id) coming through CDC via Debezium
        Serde<String> stringCdcKeySerdes = new ChangeEventAwareJsonSerde<>(String.class);
        stringCdcKeySerdes.configure(Collections.emptyMap(), true);

        // Serdes for the deviceinfo message payload coming through CDC via Debezium
        Serde<DeviceInfo> deviceInfoCdcSerdes = new ChangeEventAwareJsonSerde<>(DeviceInfo.class);
        deviceInfoCdcSerdes.configure(Collections.emptyMap(), false);
        
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, DeviceTelemetry> deviceTelemetry = 
            builder.stream("device-telemetry", Consumed.with(deviceTelemetryKeySerdes, deviceTelemetrySerdes));

        KTable<String, DeviceInfo> deviceInfo = 
            builder.table("dbserver1.devices.deviceinfo", Consumed.with(stringCdcKeySerdes, deviceInfoCdcSerdes));
        
        deviceTelemetry.join(deviceInfo, (telemetry, info) -> {
            log.info("info = {}, telemetry = {}", info, telemetry);
            telemetry.manufacturer = info.manufacturer;
            return telemetry;
        }).to("device-telemetry-enriched", Produced.with(Serdes.String(), deviceTelemetrySerdes));
        
        KafkaStreams streams = new KafkaStreams(builder.build(), props);

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
    }
}
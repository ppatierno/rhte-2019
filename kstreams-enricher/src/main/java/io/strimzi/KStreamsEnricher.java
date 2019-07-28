package io.strimzi;

import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        
        StreamsBuilder builder = new StreamsBuilder();        

        KafkaStreams streams = new KafkaStreams(builder.build(), props);

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
    }
}
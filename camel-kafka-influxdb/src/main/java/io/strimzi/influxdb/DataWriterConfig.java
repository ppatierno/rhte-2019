package io.strimzi.influxdb;

import java.util.Map;

public class DataWriterConfig {

    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String DEFAULT_DATABASE_URL = "http://localhost:8086";
    private static final String DEFAULT_DATABASE = "sensor";
    private static final String DEFAULT_MEASUREMENT = "device-data";
    private static final String DEFAULT_TOPIC_DEVICE_DATA = "device-telemetry-enriched";
    private static final String DEFAULT_CONSUMER_GROUP = "camel-kafka-influxdb";

    private static final String BOOTSTRAP_SERVERS = "BOOTSTRAP_SERVERS";
    private static final String DATABASE_URL = "DATABASE_URL";
    private static final String DATABASE = "DATABASE";
    private static final String MEASUREMENT = "MEASUREMENT";
    private static final String TOPIC_DEVICE_DATA = "TOPIC_DEVICE_DATA";
    private static final String CONSUMER_GROUP = "CONSUMER_GROUP";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    private final String bootstrapServers;
    private final String databaseUrl;
    private final String database;
    private final String measurement;
    private final String topicDeviceData;
    private final String consumerGroup;
    private final String username;
    private final String password;

    public DataWriterConfig(String bootstrapServers, String databaseUrl, String database, String measurement, String topicDeviceData, String consumerGroup, String username, String password) {
        this.bootstrapServers = bootstrapServers;
        this.databaseUrl = databaseUrl;
        this.database = database;
        this.measurement = measurement;
        this.topicDeviceData = topicDeviceData;
        this.consumerGroup = consumerGroup;
        this.username = username;
        this.password = password;
    }

    /**
     * Loads data writer configuration from a related map
     *
     * @param map map from which loading configuration parameters
     * @return DataWriter configuration instance
     */
    public static DataWriterConfig fromMap(Map<String, String> map) {

        String bootstrapServers = map.getOrDefault(DataWriterConfig.BOOTSTRAP_SERVERS, DEFAULT_BOOTSTRAP_SERVERS);
        String databaseUrl = map.getOrDefault(DataWriterConfig.DATABASE_URL, DEFAULT_DATABASE_URL);
        String database = map.getOrDefault(DataWriterConfig.DATABASE, DEFAULT_DATABASE);
        String measurement = map.getOrDefault(DataWriterConfig.MEASUREMENT, DEFAULT_MEASUREMENT);
        String topicDeviceData = map.getOrDefault(DataWriterConfig.TOPIC_DEVICE_DATA, DEFAULT_TOPIC_DEVICE_DATA);
        String consumerGroup = map.getOrDefault(DataWriterConfig.CONSUMER_GROUP, DEFAULT_CONSUMER_GROUP);
        String username = map.getOrDefault(DataWriterConfig.USERNAME, null);
        String password = map.getOrDefault(DataWriterConfig.PASSWORD, null);

        return new DataWriterConfig(bootstrapServers, databaseUrl, database, measurement, topicDeviceData, consumerGroup, username, password);
    }

    public String bootstrapServers() {
        return this.bootstrapServers;
    }

    public String databaseUrl() {
        return this.databaseUrl;
    }

    public String database() {
        return this.database;
    }

    public String measurement() {
        return this.measurement;
    }

    public String topicDeviceData() {
        return this.topicDeviceData;
    }

    public String consumerGroup() {
        return this.consumerGroup;
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }

    @Override
    public String toString() {
        return "DataWriterConfig(" +
                ",bootstrapServers=" + bootstrapServers +
                ",databaseUrl=" + databaseUrl +
                ",database=" + database +
                ",measurement=" + measurement +
                ",topicDeviceData=" + topicDeviceData +
                ",consumerGroup=" + consumerGroup +
                ",username=" + username +
                ",password=" + password +
                ")";
    }
}
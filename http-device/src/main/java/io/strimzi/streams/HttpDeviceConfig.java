package io.strimzi.streams;

import java.util.Map;

/**
 * HttpDeviceConfig
 */
public class HttpDeviceConfig {

    private static final String ENV_DEVICE_ID = "DEVICE_ID";
    private static final String ENV_HOST = "HOST";
    private static final String ENV_PORT = "PORT";
    private static final String ENV_TOPIC = "TOPIC";
    private static final String ENV_SEND_INTERVAL = "SEND_INTERVAL";
    private static final String ENV_MIN_TEMPERATURE = "MIN_TEMPERATURE";
    private static final String ENV_MAX_TEMPERATURE = "MAX_TEMPERATURE";
    private static final String ENV_MIN_HUMIDITY = "MIN_HUMIDITY";
    private static final String ENV_MAX_HUMIDITY = "MAX_HUMIDITY";

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_TOPIC = "device-telemetry";
    private static final int DEFAULT_SEND_INTERVAL = 1000;
    private static final int DEFAULT_MIN_TEMPERATURE = 20;
    private static final int DEFAULT_MAX_TEMPERATURE = 25;
    private static final int DEFAULT_MIN_HUMIDITY = 50;
    private static final int DEFAULT_MAX_HUMIDITY = 55;

    private final String deviceId;
    private final String host;
    private final int port;
    private final String topic;
    private final int sendInterval;
    private final int minTemperature;
    private final int maxTemperature;
    private final int minHumidity;
    private final int maxHumidity;

    /**
     * Constructor
     * 
     * @param deviceId device ID
     * @param host host to which connect to
     * @param port host port to which connect to
     * @param topic Kafka topic from which consume messages
     * @param sendInterval interval (in ms) for sending messages
     * @param minTemperature minimum generated temperature
     * @param maxTemperature maximum generated temperature
     * @param minHumidity minimum generated humidity
     * @param maxHumidity maximum generated humidity
     */
    private HttpDeviceConfig(String deviceId, String host, int port, 
                             String topic, int sendInterval, 
                             int minTemperature, int maxTemperature,
                             int minHumidity, int maxHumidity) {
        this.deviceId = deviceId;
        this.host = host;
        this.port = port;
        this.topic = topic;
        this.sendInterval = sendInterval;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
    }

    /**
     * @return the device ID
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * @return host to which connect to
     */
    public String getHost() {
        return host;
    }

    /**
     * @return host port to which connect to
     */
    public int getPort() {
        return port;
    }

    /**
     * @return Kafka topic to send messages to
     */
    public String getTopic() {
        return topic;
    }

    /**
     * @return interval (in ms) for sending messages
     */
    public int getSendInterval() {
        return sendInterval;
    }

    /**
     * @return minimum generated temperature
     */
    public int getMinTemperature() {
        return minTemperature;
    }

    /**
     * @return maximum generated temperature
     */
    public int getMaxTemperature() {
        return maxTemperature;
    }

    /**
     * @return minimum generated humidity
     */
    public int getMinHumidity() {
        return minHumidity;
    }

    /**
     * @return maximum generated humidity
     */
    public int getMaxHumidity() {
        return maxHumidity;
    }

    /**
     * Load all HTTP device configuration parameters from a related map
     * 
     * @param map map from which loading configuration parameters
     * @return HTTP device configuration
     */
    public static HttpDeviceConfig fromMap(Map<String, Object> map) {
        String deviceId = (String) map.get(ENV_DEVICE_ID);
        if (deviceId == null) {
            throw new IllegalArgumentException("Device id is mandatory!");
        }
        String host = (String) map.getOrDefault(ENV_HOST, DEFAULT_HOST);
        int port = Integer.parseInt(map.getOrDefault(ENV_PORT, DEFAULT_PORT).toString());
        String topic = (String) map.getOrDefault(ENV_TOPIC, DEFAULT_TOPIC);
        int sendInterval = Integer.parseInt(map.getOrDefault(ENV_SEND_INTERVAL, DEFAULT_SEND_INTERVAL).toString());
        int minTemperature = Integer.parseInt(map.getOrDefault(ENV_MIN_TEMPERATURE, DEFAULT_MIN_TEMPERATURE).toString());
        int maxTemperature = Integer.parseInt(map.getOrDefault(ENV_MAX_TEMPERATURE, DEFAULT_MAX_TEMPERATURE).toString());
        int minHumidity = Integer.parseInt(map.getOrDefault(ENV_MIN_HUMIDITY, DEFAULT_MIN_HUMIDITY).toString());
        int maxHumidity = Integer.parseInt(map.getOrDefault(ENV_MAX_HUMIDITY, DEFAULT_MAX_HUMIDITY).toString());
        return new HttpDeviceConfig(deviceId, host, port, topic, sendInterval, minTemperature, maxTemperature, minHumidity, maxHumidity);
    }

    @Override
    public String toString() {
        return "HttpDeviceConfig(" +
                "deviceId=" + this.deviceId +
                ",host=" + this.host +
                ",port=" + this.port +
                ",topic=" + this.topic +
                ",sendInterval=" + this.sendInterval +
                ",minTemperature=" + this.minTemperature +
                ",maxTemperature=" + this.maxTemperature +
                ",minHumidity=" + this.minHumidity +
                ",maxHumidity=" + this.maxHumidity +
                ")";
    }
}
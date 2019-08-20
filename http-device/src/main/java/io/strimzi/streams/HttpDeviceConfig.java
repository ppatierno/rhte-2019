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
    private static final String ENV_MIN_TEMP = "MIN_TEMP";
    private static final String ENV_MAX_TEMP = "MAX_TEMP";

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_TOPIC = "device-telemetry";
    private static final int DEFAULT_SEND_INTERVAL = 1000;
    private static final int DEFAULT_MIN_TEMP = 0;
    private static final int DEFAULT_MAX_TEMP = 100;

    private final String deviceId;
    private final String host;
    private final int port;
    private final String topic;
    private final int sendInterval;
    private final int minTemp;
    private final int maxTemp;

    /**
     * Constructor
     * 
     * @param deviceId device ID
     * @param host host to which connect to
     * @param port host port to which connect to
     * @param topic Kafka topic from which consume messages
     * @param sendInterval interval (in ms) for sending messages
     */
    private HttpDeviceConfig(String deviceId, String host, int port, 
                             String topic, int sendInterval, int minTemp, int maxTemp) {
        this.deviceId = deviceId;
        this.host = host;
        this.port = port;
        this.topic = topic;
        this.sendInterval = sendInterval;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
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
     * @return minimal generated temperature
     */
    public int getMinTemp() {
        return minTemp;
    }

    /**
     * @return maximal generated temperature
     */
    public int getMaxTemp() {
        return maxTemp;
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
        int minTemp = Integer.parseInt(map.getOrDefault(ENV_MIN_TEMP, DEFAULT_MIN_TEMP).toString());
        int maxTemp = Integer.parseInt(map.getOrDefault(ENV_MAX_TEMP, DEFAULT_MAX_TEMP).toString());
        return new HttpDeviceConfig(deviceId, host, port, topic, sendInterval, minTemp, maxTemp);
    }

    @Override
    public String toString() {
        return "HttpDeviceConfig(" +
                "deviceId=" + this.deviceId +
                ",host=" + this.host +
                ",port=" + this.port +
                ",topic=" + this.topic +
                ",sendInterval=" + this.sendInterval +
                ",minTemp=" + this.minTemp +
                ",maxTemp=" + this.maxTemp +
                ")";
    }
}
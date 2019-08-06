package io.strimzi.streams;

import java.util.Map;

/**
 * KStreamsEnricherConfig
 */
public class KStreamsEnricherConfig {

    private static final String ENV_APPLICATION_ID = "APPLICATION_ID";
    private static final String ENV_BOOTSTRAP_SERVERS = "BOOTSTRAP_SERVERS";
    private static final String ENV_USERNAME = "USERNAME";
    private static final String ENV_PASSWORD = "PASSWORD";

    private static final String DEFAULT_APPLICATION_ID = "kstreams-enricher";
    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String DEFAULT_USERNAME = null;
    private static final String DEFAULT_PASSWORD = null;

    private final String applicationId;
    private final String bootstrapServers;
    private final String username;
    private final String password;

    /**
     * Constructor
     * 
     * @param applicationId application id
     * @param bootstrapServers bootstrap servers to connect to
     * @param username username for authentication
     * @param password password for authentication
     */
    private KStreamsEnricherConfig(String applicationId, String bootstrapServers, String username, String password) {
        this.applicationId = applicationId;
        this.bootstrapServers = bootstrapServers;
        this.username = username;
        this.password = password;
    }

    public static KStreamsEnricherConfig fromMap(Map<String, String> map) {
        String applicationId = (String) map.getOrDefault(ENV_APPLICATION_ID, DEFAULT_APPLICATION_ID);
        String bootstrapServers = (String) map.getOrDefault(ENV_BOOTSTRAP_SERVERS, DEFAULT_BOOTSTRAP_SERVERS);
        String username = (String) map.getOrDefault(ENV_USERNAME, DEFAULT_USERNAME);
        String password = (String) map.getOrDefault(ENV_PASSWORD, DEFAULT_PASSWORD);
        return new KStreamsEnricherConfig(applicationId, bootstrapServers, username, password);
    }

    /**
     * @return application id
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * @return bootstrap servers to connect to
     */
    public String getBootstrapServers() {
        return bootstrapServers;
    }

    /**
     * @return username for authentication
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return password for authentication
     */
    public String getPassword() {
        return password;
    }
    
    @Override
    public String toString() {
        return "KStreamsEnricherConfig(" +
                "applicationId=" + this.applicationId +
                ",bootstrapServers=" + this.bootstrapServers +
                ",username=" + this.username + 
                ",password=" + this.password +
                ")";
    }
}
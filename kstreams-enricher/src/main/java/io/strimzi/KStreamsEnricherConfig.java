package io.strimzi;

import java.util.Map;

/**
 * KStreamsEnricherConfig
 */
public class KStreamsEnricherConfig {

    private static final String ENV_APPLICATION_ID = "APPLICATION_ID";
    private static final String ENV_BOOTSTRAP_SERVERS = "BOOTSTRAP_SERVERS";

    private static final String DEFAULT_APPLICATION_ID = "kstreams-enricher";
    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";

    private final String applicationId;
    private final String bootstrapServers;

    /**
     * Constructor
     * 
     * @param applicationId application id
     * @param bootstrapServers bootstrap servers to connect to
     */
    private KStreamsEnricherConfig(String applicationId, String bootstrapServers) {
        this.applicationId = applicationId;
        this.bootstrapServers = bootstrapServers;
    }

    public static KStreamsEnricherConfig fromMap(Map<String, String> map) {
        String applicationId = (String) map.getOrDefault(ENV_APPLICATION_ID, DEFAULT_APPLICATION_ID);
        String bootstrapServers = (String) map.getOrDefault(ENV_BOOTSTRAP_SERVERS, DEFAULT_BOOTSTRAP_SERVERS);
        return new KStreamsEnricherConfig(applicationId, bootstrapServers);
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
    
    @Override
    public String toString() {
        return "KStreamsEnricherConfig(" +
                "applicationId=" + this.applicationId +
                ",bootstrapServers=" + this.bootstrapServers +
                ")";
    }
}
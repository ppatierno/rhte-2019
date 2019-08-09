package io.strimzi.streams;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * HttpDevice
 */
public class HttpDeviceApp {

    private static final String ENV_DEVICE_IDS = "DEVICE_IDS";

    private static final Logger log = LoggerFactory.getLogger(HttpDeviceApp.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        ConfigStoreOptions envStore = new ConfigStoreOptions()
                .setType("env")
                .setConfig(new JsonObject().put("raw-data", true));

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(envStore);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

        retriever.getConfig(ar -> {
            Map<String, Object> envConfig = ar.result().getMap();

            String deviceIds = (String) envConfig.get(ENV_DEVICE_IDS);
            if (deviceIds == null) {
                log.error("Device ids are mandatory!");
                System.exit(1);
            }
            String[] deviceIdsList = deviceIds.split(";");
            for (String deviceId : deviceIdsList) {
                envConfig.put("DEVICE_ID", deviceId);
                HttpDeviceConfig httpDeviceConfig = HttpDeviceConfig.fromMap(envConfig);

                HttpDevice httpDevice = new HttpDevice(httpDeviceConfig);

                vertx.deployVerticle(httpDevice, done -> {
                    if (done.succeeded()) {
                        log.info("HTTP device {} started successfully", httpDeviceConfig.getDeviceId());
                    } else {
                        log.error("Failed to deploy HTTP device {}", httpDeviceConfig.getDeviceId(), done.cause());
                        System.exit(1);
                    }
                });
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                vertx.close();
            }
        });
    }
}
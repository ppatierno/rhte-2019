package io.strimzi.streams.model;

/**
 * DeviceTelemetry
 */
public class DeviceTelemetry {

    public String deviceId;
    public int temperature;

    public DeviceTelemetry() {

    }

    public DeviceTelemetry(String deviceId, int temperature) {
        this.deviceId = deviceId;
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "DeviceTelemetry(" +
                "deviceId = " + this.deviceId +
                ", temperature = " + this.temperature +
                ")";
    }
}
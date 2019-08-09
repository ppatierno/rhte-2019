package io.strimzi.streams.model;

/**
 * DeviceTelemetry
 */
public class DeviceTelemetry {

    public String deviceId;
    public int temperature;
    public String manufacturer;

    public DeviceTelemetry() {

    }

    public DeviceTelemetry(String deviceId, int temperature, String manufacturer) {
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.manufacturer = manufacturer;
    }

    @Override
    public String toString() {
        return "DeviceTelemetry(" +
                "deviceId = " + this.deviceId +
                ",temperature = " + this.temperature +
                ",manufacturer = " + this.manufacturer +
                ")";
    }
}
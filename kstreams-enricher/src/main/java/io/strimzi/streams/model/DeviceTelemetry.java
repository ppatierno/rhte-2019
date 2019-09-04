package io.strimzi.streams.model;

/**
 * DeviceTelemetry
 */
public class DeviceTelemetry {

    public String deviceId;
    public int temperature;
    public int humidity;
    public String manufacturer;

    public DeviceTelemetry() {

    }

    public DeviceTelemetry(String deviceId, int temperature, int humidity, String manufacturer) {
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.manufacturer = manufacturer;
    }

    @Override
    public String toString() {
        return "DeviceTelemetry(" +
                "deviceId = " + this.deviceId +
                ",temperature = " + this.temperature +
                ",humidity = " + this.humidity +
                ",manufacturer = " + this.manufacturer +
                ")";
    }
}
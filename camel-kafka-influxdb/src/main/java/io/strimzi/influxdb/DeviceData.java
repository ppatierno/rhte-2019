package io.strimzi.influxdb;

public class DeviceData {

    public String deviceId;
    public int temperature;
    public int humidity;
    public String manufacturer;
    public String owner;

    public DeviceData() {

    }

    public DeviceData(String deviceId, int temperature, int humidity, String manufacturer, String owner) {
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.manufacturer = manufacturer;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "DeviceData(" +
                "deviceId = " + this.deviceId +
                ",temperature = " + this.temperature +
                ",humidity = " + this.humidity +
                ",manufacturer = " + this.manufacturer +
                ",owner = " + this.owner +
                ")";
    }
}
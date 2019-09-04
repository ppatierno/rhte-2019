package io.strimzi.influxdb;

public class DeviceData {

    public String deviceId;
    public int temperature;
    public int humidity;
    public String manufacturer;

    public DeviceData() {

    }

    public DeviceData(String deviceId, int temperature, int humidity, String manufacturer) {
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.manufacturer = manufacturer;
    }

    @Override
    public String toString() {
        return "DeviceData(" +
                "deviceId = " + this.deviceId +
                ",temperature = " + this.temperature +
                ",humidity = " + this.humidity +
                ",manufacturer = " + this.manufacturer +
                ")";
    }
}
package io.strimzi.influxdb;

public class DeviceData {

    public String deviceId;
    public int temperature;
    public String manufacturer;

    public DeviceData() {

    }

    public DeviceData(String deviceId, int temperature, String manufacturer) {
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.manufacturer = manufacturer;
    }

    @Override
    public String toString() {
        return "DeviceData(" +
                "deviceId = " + this.deviceId +
                ",temperature = " + this.temperature +
                ",manufacturer = " + this.manufacturer +
                ")";
    }
}
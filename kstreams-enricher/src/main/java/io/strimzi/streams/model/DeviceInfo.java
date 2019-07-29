package io.strimzi.streams.model;

/**
 * DeviceInfo
 */
public class DeviceInfo {

    public String deviceId;
    public String manufacturer;

    public DeviceInfo() {
        
    }
    
    public DeviceInfo(String deviceId, String manufacturer) {
        this.deviceId = deviceId;
        this.manufacturer = manufacturer;
    }

    @Override
    public String toString() {
        return "DeviceInfo(" +
                "deviceId = " + this.deviceId +
                ",manufacturer = " + this.manufacturer +
                ")";
    }
}
package io.strimzi.streams.model;

/**
 * DeviceInfo
 */
public class DeviceInfo {

    public String id;
    public String manufacturer;

    public DeviceInfo() {
        
    }
    
    public DeviceInfo(String id, String manufacturer) {
        this.id = id;
        this.manufacturer = manufacturer;
    }

    @Override
    public String toString() {
        return "DeviceInfo(" +
                "id = " + this.id +
                ",manufacturer = " + this.manufacturer +
                ")";
    }
}
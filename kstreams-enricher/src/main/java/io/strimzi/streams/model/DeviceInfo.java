package io.strimzi.streams.model;

/**
 * DeviceInfo
 */
public class DeviceInfo {

    public String id;
    public String manufacturer;
    public String owner;

    public DeviceInfo() {
        
    }
    
    public DeviceInfo(String id, String manufacturer, String owner) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "DeviceInfo(" +
                "id = " + this.id +
                ",manufacturer = " + this.manufacturer +
                ",owner = " + this.owner +
                ")";
    }
}
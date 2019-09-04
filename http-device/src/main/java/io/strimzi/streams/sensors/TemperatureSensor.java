package io.strimzi.streams.sensors;

/**
 * Interface for sensors providing temperature value
 */
public interface TemperatureSensor extends Sensor {

    /**
     * Return the read temperature value
     *
     * @return temperature value
     */
    int getTemperature();
}
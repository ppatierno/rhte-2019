package io.strimzi.streams.sensors;

public interface HumiditySensor extends Sensor {

    /**
     * Return the read humidity value
     *
     * @return  humidity value
     */
    int getHumidity();
}
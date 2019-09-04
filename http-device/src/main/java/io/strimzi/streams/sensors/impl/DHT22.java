package io.strimzi.streams.sensors.impl;

import java.util.Properties;
import java.util.Random;

import io.strimzi.streams.sensors.HumiditySensor;
import io.strimzi.streams.sensors.TemperatureSensor;

/**
 * Simulated DHT22 temperature and humidity sensor
 * 
 * @see <a href="https://learn.adafruit.com/dht/overview">DHTxx family</a>
 */
public class DHT22 implements TemperatureSensor, HumiditySensor {

    public static final String MIN_TEMPERATURE = "minTemperature";
    public static final String MAX_TEMPERATURE = "maxTemperature";
    public static final String MIN_HUMIDITY = "minHumidity";
    public static final String MAX_HUMIDITY = "maxHumidity";

    private int minTemperature;
    private int maxTemperature;
    private int minHumidity;
    private int maxHumidity;
    private Random random = new Random();

    @Override
    public int getHumidity() {
        int humidity = this.minHumidity + random.nextInt(this.maxHumidity + 1 - this.minHumidity);
        return humidity;
    }

    @Override
    public int getTemperature() {
        int temp = this.minTemperature + random.nextInt(this.maxTemperature + 1 - this.minTemperature);
        return temp;
    }

    @Override
    public void init(Properties config) {
        this.minTemperature = Integer.valueOf(config.getProperty(DHT22.MIN_TEMPERATURE));
        this.maxTemperature = Integer.valueOf(config.getProperty(DHT22.MAX_TEMPERATURE));
        this.minHumidity = Integer.valueOf(config.getProperty(DHT22.MIN_HUMIDITY));
        this.maxHumidity = Integer.valueOf(config.getProperty(DHT22.MAX_HUMIDITY));
    }
}
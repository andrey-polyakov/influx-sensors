package no.fun.core;

import java.util.List;

/**
 * Implementations manage multiple sensors of one kind.
 *
 * @see no.fun.core.bme280.BME280Manager
 * @param <T> hardware identifier
 * @param <V> readings
 */
public interface SensorManager<T, V> {

    /**
     * Adds new sensor
     * @param id
     */
    void addSensor(T id);

    /**
     * Removes a sensor
     * @param id
     */
    void removeSensor(T id);

    /**
     * Automatic probing logic to be implemented here.
     * @return list of detected sensor addresses
     */
    List<T> autoDetect();

    /**
     * Polls given sensor.
     *
     * @param id sensor's address
     * @return reading
     */
    V getReadings(T id);
}

package no.fun.core.stub;

import no.fun.core.I2cId;
import no.fun.core.SensorManager;
import no.fun.core.bme280.BME280Readings;

import java.util.HashSet;
import java.util.Set;

public class StubSensorManager implements SensorManager<I2cId, BME280Readings> {

    Set<I2cId> keys = new HashSet<>();

    public StubSensorManager() {
        keys.add(new I2cId(1, 3));
    }

    @Override
    public void addSensor(I2cId id) {

    }

    @Override
    public void removeSensor(I2cId id) {

    }

    @Override
    public void autoDetect() {

    }

    @Override
    public BME280Readings getReadings(I2cId id) {
        return new BME280Readings(id.toString(), 1.1f, 1.4f, 2.4f);
    }

    @Override
    public Set<I2cId> getKeys() {
        return keys;
    }
}

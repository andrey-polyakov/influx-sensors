package no.fun.core.bme280;

import com.diozero.devices.BME280;
import com.diozero.util.RuntimeIOException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.fun.core.I2cId;
import no.fun.core.SensorManager;
import no.fun.core.exceptions.ConfigurationError;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages I2C based BME280 sensors.
 */
@Slf4j
@Component
public class BME280Manager implements SensorManager<I2cId, BME280Readings> {
    private final Map<I2cId, BME280> sensors = new ConcurrentHashMap<>();

    /**
     * Inspects /sys/class/i2c-dev/ to decide what buses are available. Bus Adapter numbers are assigned
     * somewhat dynamically, so there is nothing much to assume much about them. They can even change
     * from one boot to the next.
     *
     * @return buses
     */
    public List<Integer> scanAdapters() {
        List<Integer> buses = new LinkedList<>();
        String devDirectory = "/sys/class/i2c-dev/";
        Stream<Path> p;
        try {
            p = Files.list(Paths.get(devDirectory));
        } catch (Exception e1) {
            log.error("There is a problem accessing device files in {}", devDirectory, e1);
            return Collections.EMPTY_LIST;
        }
        for (Path name : p.collect(Collectors.toList())) {
            try {
                buses.add(new Integer(name.getName(name.getNameCount() - 1).toString().split("-")[1]));
            } catch (NumberFormatException n) {
                log.error("Failed to extract I2C bus number from its file name {}", name.getFileName().toString());
            }
        }
        return buses;
    }

    @Override
    public void addSensor(I2cId id) {
        BME280 device = null;
        try {
            device = new BME280(id.getBus(), id.getAddress());
            sensors.putIfAbsent(id, device);
            log.info("Successfully added a BME280 at {}", id.toString());
        } catch (LinkageError le) {
            if (device != null)
                device.close();
            throw new ConfigurationError("SMBUS may not installed or configured on this system");
        } catch (RuntimeIOException e) {
            if (device != null)
                device.close();
            throw new ConfigurationError("No sensor at given address");
        }
    }

    @Override
    public void removeSensor(I2cId id) {
        BME280 sensor = sensors.get(id);
        sensors.remove(id);
        if (sensor != null) {
            sensor.close();
        }
    }

    /**
     * Probes all addresses across I2C buses for BME280 sensor. Tracks all discovered devices.
     */
    @PostConstruct
    @Override
    public void autoDetect() {
        for (int bus : scanAdapters()) {
            log.info("Probing I2C bus #{}", bus);
            for (int address = 1; address < 128; address++) {
                try {
                    addSensor(new I2cId(bus, address));
                } catch (Error ignore) {
                    //keep on going on best effort basis
                }
            }
        }
        log.info("Discovered total of {} devices", sensors.size());
    }

    @Override
    public BME280Readings getReadings(I2cId id) {
        BME280 s = sensors.get(id);
        if (s == null) {
            return null;
        }
        return new BME280Readings(id.toString(), s.getValues());
    }

    @Override
    public Set<I2cId> getKeys() {
        return sensors.keySet();
    }

}

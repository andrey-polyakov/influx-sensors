package no.fun.core;

import com.diozero.devices.BME280;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import no.fun.core.exceptions.ConfigurationError;
import org.influxdb.dto.Point;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class BME280Daemon {
    private ExecutorService e = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private InfluxDBTemplate<Point> influxDBTemplate;
    private final Map<SensorId, BME280> sensors = new ConcurrentHashMap<>();

    private Runnable mainThread = () -> {
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            for (SensorId sensorId : sensors.keySet()) {

            }
        }
    };

    public void addSensor(SensorId id) {
        try {
            sensors.putIfAbsent(id, new BME280(id.bus, id.address));
        } catch (LinkageError le) {
            throw new ConfigurationError("It seems that SMBUS is not installed on this system");
        }
    }

    public Map getReadings(SensorId id) {
        BME280 s = sensors.get(id);
        if (s == null) {
            return Collections.EMPTY_MAP;
        }
        Map<String, Float> readings = new HashMap<>();
        readings.put("humidity", s.getRelativeHumidity());
        readings.put("pressure", s.getPressure());
        readings.put("temperature", s.getTemperature());
        return readings;
    }

    public void readSycle() {

    }

    @EqualsAndHashCode
    @AllArgsConstructor
    public static class SensorId {
        private Integer bus, address;
    }

    @AllArgsConstructor
    public static class Readings {
        private Float temperature, pressure, humidity;

        public Readings(float[] values) {
            if (values.length != 3) {
                return;
            }
            temperature = values[0];
            pressure = values[1];
            humidity = values[2];

        }
    }

}

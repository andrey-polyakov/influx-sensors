package no.fun.core;

import lombok.extern.slf4j.Slf4j;
import no.fun.core.bme280.BME280Manager;
import no.fun.core.bme280.BME280Readings;
import no.fun.core.writers.InfluxDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Collector which runs multiple workers to persist readings.
 */
@Slf4j
@Component
public class Collector {
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();
    private BME280Manager sensors;
    private InfluxDb influx;

    @Autowired
    public Collector(BME280Manager sensors, InfluxDb influx) {
        this.sensors = sensors;
        this.influx = influx;
    }

    @PostConstruct
    private void onStarted() {
        List<I2cId> addresses = sensors.autoDetect();
        for (I2cId address : addresses) {
            scheduledFutures.add(scheduledExecutorService.scheduleAtFixedRate(
                    () -> {
                        BME280Readings r = sensors.getReadings(address);
                        log.info("Sampled Temperature {}, sampled Humidity {}, sampled pressure {}",
                                r.getTemperature(),
                                r.getHumidity(),
                                r.getPressure());
                        influx.write(r);
                    }, 0, 1, TimeUnit.SECONDS));
        }

    }

    @PreDestroy
    private void onShutdown() {
        scheduledExecutorService.shutdown();
        influx.close();
    }

    private void onChangedConfiguration() {
        List<ScheduledFuture<?>> newFutures = new ArrayList<>();
        for (ScheduledFuture<?> scheduledFuture : scheduledFutures) {
            scheduledFuture.cancel(false);
            newFutures.add(scheduledExecutorService.scheduleAtFixedRate(
                    () -> {
                        //TODO
                    }, 0, 1, TimeUnit.SECONDS));
        }
    }
}

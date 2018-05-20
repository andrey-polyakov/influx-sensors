package no.fun.core.collectors;

import lombok.extern.slf4j.Slf4j;
import no.fun.core.I2cId;
import no.fun.core.SensorManager;
import no.fun.core.bme280.BME280Readings;
import no.fun.core.configuration.CollectorConfiguration;
import no.fun.core.writers.InfluxDbWriter;
import no.fun.core.writers.embedded.ReadingEntity;
import no.fun.core.writers.embedded.ReadingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Collector which runs multiple workers to persist readings from each sensor.
 */
@Slf4j
@Component
public class DefaultCollector implements Collector {
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();
    private SensorManager<I2cId, BME280Readings> sensorManager;
    private InfluxDbWriter influx;
    private String measurementPrefix = "greenhouse-sensor-";
    private ReadingsRepository readingsRepository;

    @Autowired
    public DefaultCollector(SensorManager<I2cId, BME280Readings> sensorManager, ReadingsRepository readingsRepository) {
        this.sensorManager = sensorManager;
        this.readingsRepository = readingsRepository;
    }

    @PostConstruct
    private void onStarted() {
        readingsRepository.save(new ReadingEntity());
        readingsRepository.count();
        log.debug("Starting default readings collector");
        influx = new InfluxDbWriter();
        influx.init();
        for (I2cId address : sensorManager.getKeys()) {
            scheduledFutures.add(scheduledExecutorService.scheduleAtFixedRate(
                    () -> {
                        String measurement = measurementPrefix + address.toString();
                        BME280Readings r = sensorManager.getReadings(address);
                        try {
                            influx.write(r, measurement);
                        } catch (Exception e) {
                            log.warn("InfluxDb write failed",e);
                        }
                        try {
                            readingsRepository.save(new ReadingEntity(measurement,
                                    r.getTemperature(),
                                    r.getHumidity(),
                                    r.getPressure()));
                        } catch (Exception e) {
                            log.warn("Embedded DB write failed",e);
                        }
                    }, 0, 1, TimeUnit.SECONDS));
        }
        log.info("Collector initialized");
    }

    @PreDestroy
    private void onShutdown() {
        scheduledExecutorService.shutdown();
        influx.close();
    }

    @Override
    public void onConfigurationChange(CollectorConfiguration configuration) {
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

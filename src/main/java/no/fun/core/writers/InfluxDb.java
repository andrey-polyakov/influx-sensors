package no.fun.core.writers;

import no.fun.core.bme280.BME280Readings;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class InfluxDb {

    private InfluxDB influxDB;
    private String dbName = "TimeSeries";
    private String measurement = "greenhouse-sensor";

    public void init() {
        influxDB = InfluxDBFactory.connect("http://172.17.0.2:8086", "root", "root");
        influxDB.createDatabase(dbName);
        influxDB.setDatabase(dbName);
        influxDB.enableBatch(BatchOptions.DEFAULTS);
    }

    public void write(BME280Readings readings) {
        influxDB.write(Point.measurement(measurement)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("humidity", readings.getHumidity())
                .addField("pressure", readings.getPressure())
                .addField("temperature", readings.getTemperature())
                .build());
    }

    public void close() {
        influxDB.close();
    }

}

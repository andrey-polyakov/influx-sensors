package no.fun.core.writers;

import lombok.extern.slf4j.Slf4j;
import no.fun.core.bme280.BME280Readings;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import java.util.concurrent.TimeUnit;

@Slf4j
public class InfluxDbWriter implements AutoCloseable {

    private InfluxDB influxDB;
    private String URL = "http://127.0.0.1:8086";
    private String dbName = "TimeSeries";
    private String dbUser = "root";
    private String dbPass = "root";

    public InfluxDbWriter(String URL, String dbName, String dbUser, String dbPass) {
        this.URL = URL;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
    }

    public InfluxDbWriter() {
        //
    }

    public void init() {
        influxDB = InfluxDBFactory.connect(URL, dbUser, dbPass);
        influxDB.setDatabase(dbName);
        influxDB.enableBatch(BatchOptions.DEFAULTS);
        log.info("InfluxDB connector initialized for {}@{}/{}", dbUser, URL, dbName);
    }

    public void write(BME280Readings readings, String measurement) {
        influxDB.write(Point.measurement(measurement)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("humidity", readings.getHumidity())
                .addField("pressure", readings.getPressure())
                .addField("temperature", readings.getTemperature())
                .build());
    }

    @Override
    public void close() {
        influxDB.close();
    }

}

package no.fun.api;

import io.swagger.annotations.Api;
import no.fun.core.I2cId;
import no.fun.core.SensorManager;
import no.fun.core.bme280.BME280Readings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@Api(value="SENSORS", description="Allow manipulations with sensors.")
@RestController
@RequestMapping("v0/bme280")
public class Sensors {

    private SensorManager<I2cId, BME280Readings> sensorManager;

    @Autowired
    public Sensors(SensorManager<I2cId, BME280Readings> daemon) {
        this.sensorManager = daemon;
    }

    @PostMapping("{bus}-{address}")
    public void add(@Valid @PathVariable Integer bus, @Valid @PathVariable Integer address) {
        sensorManager.addSensor(new I2cId(bus, address));
    }

    @GetMapping("{bus}-{address}")
    public BME280Readings read(@Valid @PathVariable Integer bus, @Valid @PathVariable Integer address) {
        return sensorManager.getReadings(new I2cId(bus, address));
    }

    @GetMapping()
    public Set<I2cId> getAddresses() {
        return sensorManager.getKeys();
    }

}

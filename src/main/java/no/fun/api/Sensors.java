package no.fun.api;

import io.swagger.annotations.Api;
import no.fun.core.I2cId;
import no.fun.core.bme280.BME280Manager;
import no.fun.core.bme280.BME280Readings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value="SENSORS", description="Allow manipulations with sensors.")
@RestController
@RequestMapping("v0/bme280")
public class Sensors {

    private BME280Manager daemon;

    @Autowired
    public Sensors(BME280Manager daemon) {
        this.daemon = daemon;
    }

    @PostMapping("{bus}-{address}")
    public void add(@Valid @PathVariable Integer bus, @Valid @PathVariable Integer address) {
        daemon.addSensor(new I2cId(bus, address));
    }

    @GetMapping("{bus}-{address}")
    public BME280Readings read(@Valid @PathVariable Integer bus, @Valid @PathVariable Integer address) {

        return daemon.getReadings(new I2cId(bus, address));
    }


}

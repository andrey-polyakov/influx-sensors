package no.fun.api;

import io.swagger.annotations.Api;
import no.fun.core.BME280Daemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(value="SENSORS", description="Allow manipulations with sensors.")
@RestController
@RequestMapping("v0/")
public class Sensors {

    private BME280Daemon daemon;

    @Autowired
    public Sensors(BME280Daemon daemon) {
        this.daemon = daemon;
    }

    @PostMapping("{bus}/{address}")
    public void add(@RequestParam Integer bus, @RequestParam Integer address) {
        daemon.addSensor(new BME280Daemon.SensorId(bus, address));
    }

    @GetMapping("{bus}/{address}")
    public Map read(@RequestParam Integer bus, @RequestParam Integer address) {
        return daemon.getReadings(new BME280Daemon.SensorId(bus, address));
    }


}

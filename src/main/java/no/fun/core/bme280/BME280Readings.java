package no.fun.core.bme280;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * BME280 produces three values which to be packed in this class.
 */
@Data
@AllArgsConstructor
public class BME280Readings {
    private String address;
    private Float temperature, pressure, humidity;

    public BME280Readings(String name, float[] values) {
        address = name;
        if (values.length != 3) {
            return;
        }
        temperature = values[0];
        pressure = values[1];
        humidity = values[2];
    }
}

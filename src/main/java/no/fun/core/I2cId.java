package no.fun.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Generic I2C identifier comprised of bus number and address.
 */
@Data
@EqualsAndHashCode
@AllArgsConstructor
public class I2cId {
    private Integer bus, address;

    public I2cId() {
        //
    }

    @Override
    public String toString() {
        return "I2C-" + bus + "-" + address;
    }
}

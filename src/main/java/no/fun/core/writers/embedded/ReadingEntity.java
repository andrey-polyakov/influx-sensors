package no.fun.core.writers.embedded;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "readings")
public class ReadingEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime timestamp = ZonedDateTime.now();
    private String measurement;

    private Float temperature;
    private Float humidity;
    private Float pressure;

    public ReadingEntity() {
        //
    }

    public ReadingEntity(String measurement, Float temperature, Float humidity, Float pressure) {
        this.measurement = measurement;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }
}

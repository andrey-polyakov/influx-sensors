package no.fun.spring;

import no.fun.core.SensorManager;
import no.fun.core.stub.StubSensorManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("stub")
public class StubConfiguration {

    @Bean
    public SensorManager getSensorManager() {
        return new StubSensorManager();
    }

}

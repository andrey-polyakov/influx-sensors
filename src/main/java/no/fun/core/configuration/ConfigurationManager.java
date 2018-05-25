package no.fun.core.configuration;

import lombok.extern.slf4j.Slf4j;
import no.fun.core.collectors.Collector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class ConfigurationManager {
    @Value("${collector.interval:5}")
    private Integer collectorInterval;

    @PostConstruct
    public void postInit() {
        log.info(getConfiguration().toString());
    }

    public void registerCollector(Collector collector) {

    }

    public CollectorConfiguration getConfiguration() {
        CollectorConfiguration c = new CollectorConfiguration();
        c.setInterval(collectorInterval);
        return c;
    }
}

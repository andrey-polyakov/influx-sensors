package no.fun.core.collectors;

import no.fun.core.configuration.CollectorConfiguration;

public interface Collector {
    void onConfigurationChange(CollectorConfiguration configuration);
}

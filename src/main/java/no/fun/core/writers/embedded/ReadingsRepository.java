package no.fun.core.writers.embedded;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Database repository of sensor readings table with filtering support.
 * <p/>
 */
public interface ReadingsRepository extends JpaRepository<ReadingEntity, Long> {


}

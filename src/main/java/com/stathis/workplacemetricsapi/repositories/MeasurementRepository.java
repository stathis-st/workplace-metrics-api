package com.stathis.workplacemetricsapi.repositories;

import com.stathis.workplacemetricsapi.domain.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
}

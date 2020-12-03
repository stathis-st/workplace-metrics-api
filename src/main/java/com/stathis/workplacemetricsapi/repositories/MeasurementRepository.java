package com.stathis.workplacemetricsapi.repositories;

import com.stathis.workplacemetricsapi.domain.Department;
import com.stathis.workplacemetricsapi.domain.Measurement;
import com.stathis.workplacemetricsapi.domain.Metric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    Page<Measurement> findMeasurementsByMetricAndDepartmentAndMeasurementTimestampBetween(Pageable pageable,
                                                                                          Metric metric,
                                                                                          Department department,
                                                                                          ZonedDateTime fromDateTime,
                                                                                          ZonedDateTime toDateTime);
}

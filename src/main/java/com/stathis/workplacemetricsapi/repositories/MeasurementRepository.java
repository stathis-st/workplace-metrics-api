package com.stathis.workplacemetricsapi.repositories;

import com.stathis.workplacemetricsapi.domain.Department;
import com.stathis.workplacemetricsapi.domain.Measurement;
import com.stathis.workplacemetricsapi.domain.Metric;
import com.stathis.workplacemetricsapi.model.AggregatedResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    Page<Measurement> findMeasurementsByMetricAndDepartmentAndMeasurementTimestampBetween(Pageable pageable,
                                                                                          Metric metric,
                                                                                          Department department,
                                                                                          ZonedDateTime fromDateTime,
                                                                                          ZonedDateTime toDateTime);

    @Query("SELECT new com.stathis.workplacemetricsapi.model.AggregatedResult(AVG(mea.value), MIN(mea.value), MAX(mea.value)) " +
            "FROM Measurement as mea " +
            "WHERE mea.metric.id = :metricId " +
            "AND mea.department.id = :departmentId " +
            "AND mea.measurementTimestamp >= :fromDateTime " +
            "AND mea.measurementTimestamp <= :toDateTime")
    AggregatedResult getAggregatedResults(@Param("metricId") Long metricId,
                                          @Param("departmentId") Long departmentId,
                                          @Param("fromDateTime") ZonedDateTime fromDateTime,
                                          @Param("toDateTime") ZonedDateTime toDateTime);
}

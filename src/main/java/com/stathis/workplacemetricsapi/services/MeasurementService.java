package com.stathis.workplacemetricsapi.services;

import com.stathis.workplacemetricsapi.domain.Measurement;
import com.stathis.workplacemetricsapi.model.AggregatedResult;
import com.stathis.workplacemetricsapi.model.MeasurementDTO;
import com.stathis.workplacemetricsapi.model.ResponseEntityWrapper;
import org.springframework.data.domain.Pageable;

public interface MeasurementService {

    ResponseEntityWrapper<Measurement> getAllMeasurements(Pageable pageable);

    Measurement getMeasurementById(Long id);

    Measurement saveMeasurement(MeasurementDTO measurementDTO);

    ResponseEntityWrapper<Measurement> getDailyMeasurements(Pageable pageable,
                                                            Long metricId,
                                                            Long departmentId);

    AggregatedResult getDailyAggregatedResults(Long metricId, Long departmentId, Integer numberOfDaysBack);

    AggregatedResult getWeeklyAggregatedResults(Long metricId, Long departmentId, Integer numberOfWeeksBack);
}

package com.stathis.workplacemetricsapi.services;

import com.stathis.workplacemetricsapi.domain.Department;
import com.stathis.workplacemetricsapi.domain.Measurement;
import com.stathis.workplacemetricsapi.domain.Metric;
import com.stathis.workplacemetricsapi.exception.ResourceNotFoundException;
import com.stathis.workplacemetricsapi.model.AggregatedResult;
import com.stathis.workplacemetricsapi.model.MeasurementDTO;
import com.stathis.workplacemetricsapi.model.ResponseEntityWrapper;
import com.stathis.workplacemetricsapi.repositories.DepartmentRepository;
import com.stathis.workplacemetricsapi.repositories.MeasurementRepository;
import com.stathis.workplacemetricsapi.repositories.MetricRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;

import static com.stathis.workplacemetricsapi.exception.ResourceNotFoundException.RESOURCE_NOT_FOUND_WITH_ID;

@Service
@AllArgsConstructor
public class MeasurementServiceImpl implements MeasurementService {

    public static final String FAILED_TO_SAVE_MEASUREMENT_RECORD = "Failed to save measurement record: ";
    private final MeasurementRepository measurementRepository;

    private final DepartmentRepository departmentRepository;
    private final MetricRepository metricRepository;

    @Override
    public ResponseEntityWrapper<Measurement> getAllMeasurements(Pageable pageable) {
        Page<Measurement> measurementPage = measurementRepository.findAll(pageable);
        return new ResponseEntityWrapper<>(measurementPage.getContent(),
                measurementPage.getNumber(),
                measurementPage.getTotalElements(),
                measurementPage.getTotalPages());
    }

    @Override
    public Measurement getMeasurementById(Long id) {
        return measurementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_WITH_ID + id));
    }

    @Override
    public Measurement saveMeasurement(MeasurementDTO measurementDTO) {
        Department fetchedDepartment = departmentRepository.findById(measurementDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(FAILED_TO_SAVE_MEASUREMENT_RECORD +
                        "Department not found with id = " +
                        measurementDTO.getDepartmentId()));

        Metric fetchedMetric = metricRepository.findById(measurementDTO.getMetricId())
                .orElseThrow(() -> new ResourceNotFoundException(FAILED_TO_SAVE_MEASUREMENT_RECORD +
                        "Metric not found with id = " +
                        measurementDTO.getMetricId()));

        Measurement measurement = Measurement.builder()
                .value(measurementDTO.getValue())
                .measurementTimestamp(measurementDTO.getMeasurementTimestamp())
                .metric(fetchedMetric)
                .department(fetchedDepartment)
                .build();

        return measurementRepository.save(measurement);
    }

    @Override
    public ResponseEntityWrapper<Measurement> getDailyMeasurements(Pageable pageable,
                                                                   Long metricId,
                                                                   Long departmentId) {
        Metric fetchedMetric = metricRepository.findById(metricId)
                .orElseThrow(() -> new ResourceNotFoundException("No records found"));

        Department fetchedDepartment = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No records found"));

        Page<Measurement> measurementPage =
                measurementRepository.findMeasurementsByMetricAndDepartmentAndMeasurementTimestampBetween(pageable,
                        fetchedMetric,
                        fetchedDepartment,
                        ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS),
                        ZonedDateTime.now());

        return new ResponseEntityWrapper<>(measurementPage.getContent(),
                measurementPage.getNumber(),
                measurementPage.getTotalElements(),
                measurementPage.getTotalPages());
    }

    @Override
    public AggregatedResult getDailyAggregatedResults(Long metricId, Long departmentId, Integer numberOfDaysBack) {
        ZonedDateTime startOfDay = ZonedDateTime.now().minusDays(numberOfDaysBack).with(LocalTime.MIN);
        ZonedDateTime endOfDay = ZonedDateTime.now().minusDays(numberOfDaysBack).with(LocalTime.MAX);

        return measurementRepository.getAggregatedResults(metricId, departmentId, startOfDay, endOfDay);
    }

    @Override
    public AggregatedResult getWeeklyAggregatedResults(Long metricId, Long departmentId, Integer numberOfWeeksBack) {
        ZonedDateTime startOfWeek = ZonedDateTime.now().minusWeeks(3).with(WeekFields.ISO.getFirstDayOfWeek()).with(LocalTime.MIN);
        ZonedDateTime endOfWeek = startOfWeek.plusDays(6).with(LocalTime.MAX);

        return measurementRepository.getAggregatedResults(metricId, departmentId, startOfWeek, endOfWeek);
    }
}

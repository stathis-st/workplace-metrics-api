package com.stathis.workplacemetricsapi.services;

import com.stathis.workplacemetricsapi.domain.Department;
import com.stathis.workplacemetricsapi.domain.Measurement;
import com.stathis.workplacemetricsapi.domain.Metric;
import com.stathis.workplacemetricsapi.exception.ResourceNotFoundException;
import com.stathis.workplacemetricsapi.model.AggregatedResult;
import com.stathis.workplacemetricsapi.model.MeasurementDTO;
import com.stathis.workplacemetricsapi.repositories.DepartmentRepository;
import com.stathis.workplacemetricsapi.repositories.MeasurementRepository;
import com.stathis.workplacemetricsapi.repositories.MetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.stathis.workplacemetricsapi.domain.BaseEntity.ID_ONE;
import static com.stathis.workplacemetricsapi.domain.Department.ALPHA;
import static com.stathis.workplacemetricsapi.domain.Measurement.VALUE_20;
import static com.stathis.workplacemetricsapi.domain.Metric.CELSIUS;
import static com.stathis.workplacemetricsapi.domain.Metric.TEMPERATURE;
import static com.stathis.workplacemetricsapi.exception.ResourceNotFoundException.RESOURCE_NOT_FOUND_WITH_ID;
import static com.stathis.workplacemetricsapi.services.MeasurementServiceImpl.DEPARTMENT_NOT_FOUND_WITH_ID;
import static com.stathis.workplacemetricsapi.services.MeasurementServiceImpl.FAILED_TO_SAVE_MEASUREMENT_RECORD;
import static com.stathis.workplacemetricsapi.services.MeasurementServiceImpl.METRIC_NOT_FOUND_WITH_ID;
import static com.stathis.workplacemetricsapi.services.MeasurementServiceImpl.NO_MEASUREMENT_RECORDS_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

class MeasurementServiceImplTest {

    @Mock
    MeasurementRepository measurementRepository;

    @Mock
    DepartmentRepository departmentRepository;

    @Mock
    MetricRepository metricRepository;

    MeasurementService measurementService;
    DepartmentService departmentService;
    MetricService metricService;

    Measurement measurementAlphaTemperature;
    MeasurementDTO measurementDTOForSave;

    Department departmentAlpha;
    Metric metricTemperature;

    ZonedDateTime measurementDateTime;

    ZonedDateTime requestDateTime;

    LocalDate requestedDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        measurementService = new MeasurementServiceImpl(measurementRepository, departmentRepository, metricRepository);
        departmentService = new DepartmentServiceImpl(departmentRepository);
        metricService = new MetricServiceImpl(metricRepository);

        departmentAlpha = Department.builder().build();
        departmentAlpha.setId(ID_ONE);
        departmentAlpha.setName(ALPHA);

        metricTemperature = Metric.builder().build();
        metricTemperature.setId(ID_ONE);
        metricTemperature.setType(TEMPERATURE);
        metricTemperature.setMeasurementUnit(CELSIUS);

        measurementDateTime = ZonedDateTime.of(LocalDate.of(2020, 12, 2),
                LocalTime.of(20, 20, 20), ZoneId.systemDefault());

        measurementAlphaTemperature = Measurement.builder().build();
        measurementAlphaTemperature.setId(ID_ONE);
        measurementAlphaTemperature.setValue(VALUE_20);
        measurementAlphaTemperature.setMeasurementTimestamp(measurementDateTime);
        measurementAlphaTemperature.setDepartment(departmentAlpha);
        measurementAlphaTemperature.setMetric(metricTemperature);

        measurementDTOForSave = MeasurementDTO.builder()
                .value(VALUE_20)
                .measurementTimestamp(measurementDateTime)
                .metricId(metricTemperature.getId())
                .departmentId(departmentAlpha.getId())
                .build();

        requestDateTime = ZonedDateTime.of(LocalDate.of(2020, 12, 5),
                LocalTime.of(20, 20, 20), ZoneId.systemDefault());

        requestedDate = LocalDate.of(2020, 12, 2);
    }

    @Test
    void getAllMeasurements() {

        int minutesToAdd = 10;
        double measurementValue = 30.5;
        int requestedPage = 1;
        int requestedSize = 7;
        int total = 62;

        List<Measurement> measurementList = populateMeasurementList(requestedSize, minutesToAdd, measurementValue);

        Page<Measurement> measurementPage =
                new PageImpl<>(measurementList, PageRequest.of(requestedPage, requestedSize), total);

        when(measurementRepository.findAll(any(Pageable.class))).thenReturn(measurementPage);

        Page<Measurement> fetchedMeasurementPage = measurementRepository.findAll(PageRequest.of(requestedPage, requestedSize));

        assertEquals(measurementPage.getTotalPages(), fetchedMeasurementPage.getTotalPages());
        assertEquals(measurementPage.getNumber(), fetchedMeasurementPage.getNumber());
        assertEquals(measurementPage.getContent().size(), fetchedMeasurementPage.getContent().size());
        assertEquals(measurementPage.getContent().get(1).getValue(), fetchedMeasurementPage.getContent().get(1).getValue());
        assertEquals(measurementPage.getContent().get(1).getMeasurementTimestamp().getMinute(),
                fetchedMeasurementPage.getContent().get(1).getMeasurementTimestamp().getMinute());
        assertEquals(measurementPage.getContent().get(1).getMetric().getMeasurementUnit(), fetchedMeasurementPage.getContent().get(1).getMetric().getMeasurementUnit());
    }

    @Test
    void getMeasurementById() {
        when(measurementRepository.findById(anyLong())).thenReturn(Optional.ofNullable(measurementAlphaTemperature));

        Measurement fetchedMeasurement = measurementService.getMeasurementById(ID_ONE);

        assertEquals(ID_ONE, fetchedMeasurement.getId());
        assertEquals(VALUE_20, fetchedMeasurement.getValue());
        assertEquals(measurementDateTime, fetchedMeasurement.getMeasurementTimestamp());
        assertEquals(TEMPERATURE, fetchedMeasurement.getMetric().getType());
        assertEquals(CELSIUS, fetchedMeasurement.getMetric().getMeasurementUnit());
        assertEquals(ALPHA, fetchedMeasurement.getDepartment().getName());
    }

    @Test
    void getMeasurementByIdResourceNotFound() {

        when(measurementRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class, () -> measurementService.getMeasurementById(ID_ONE));

        String expectedMessage = RESOURCE_NOT_FOUND_WITH_ID + ID_ONE;

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void saveMeasurement() {

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(departmentAlpha));

        Department fetchedDepartment = departmentService.getDepartmentById(ID_ONE);

        assertEquals(fetchedDepartment.getId(), departmentAlpha.getId());
        assertEquals(fetchedDepartment.getName(), departmentAlpha.getName());

        when(metricRepository.findById(anyLong())).thenReturn(Optional.ofNullable(metricTemperature));

        Metric fetchedMetric = metricService.getMetricById(ID_ONE);

        assertEquals(fetchedMetric.getId(), metricTemperature.getId());
        assertEquals(fetchedMetric.getType(), metricTemperature.getType());
        assertEquals(fetchedMetric.getMeasurementUnit(), metricTemperature.getMeasurementUnit());

        when(measurementRepository.save(any(Measurement.class))).thenReturn(measurementAlphaTemperature);

        Measurement savedMeasurement = measurementService.saveMeasurement(measurementDTOForSave);

        assertEquals(savedMeasurement.getId(), measurementAlphaTemperature.getId());
        assertEquals(savedMeasurement.getValue(), measurementAlphaTemperature.getValue());
        assertEquals(savedMeasurement.getMeasurementTimestamp(), measurementAlphaTemperature.getMeasurementTimestamp());

        assertEquals(savedMeasurement.getMetric().getId(), measurementAlphaTemperature.getMetric().getId());
        assertEquals(savedMeasurement.getMetric().getType(), measurementAlphaTemperature.getMetric().getType());
        assertEquals(savedMeasurement.getMetric().getMeasurementUnit(), measurementAlphaTemperature.getMetric().getMeasurementUnit());

        assertEquals(savedMeasurement.getDepartment().getId(), measurementAlphaTemperature.getDepartment().getId());
        assertEquals(savedMeasurement.getDepartment().getName(), measurementAlphaTemperature.getDepartment().getName());
    }

    @Test
    void saveMeasurementDepartmentNotFound() {

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class, () -> measurementService.saveMeasurement(measurementDTOForSave));

        String expectedMessage = FAILED_TO_SAVE_MEASUREMENT_RECORD + DEPARTMENT_NOT_FOUND_WITH_ID + ID_ONE;

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void saveMeasurementMetricNotFound() {

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(departmentAlpha));

        Department fetchedDepartment = departmentService.getDepartmentById(ID_ONE);

        assertEquals(fetchedDepartment.getId(), departmentAlpha.getId());
        assertEquals(fetchedDepartment.getName(), departmentAlpha.getName());


        when(metricRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class, () -> measurementService.saveMeasurement(measurementDTOForSave));

        String expectedMessage = FAILED_TO_SAVE_MEASUREMENT_RECORD + METRIC_NOT_FOUND_WITH_ID + ID_ONE;

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getDailyMeasurementsByMetricAndDepartment() {

        when(metricRepository.findById(anyLong())).thenReturn(Optional.ofNullable(metricTemperature));

        Metric fetchedMetric = metricService.getMetricById(ID_ONE);

        assertEquals(fetchedMetric.getId(), metricTemperature.getId());
        assertEquals(fetchedMetric.getType(), metricTemperature.getType());
        assertEquals(fetchedMetric.getMeasurementUnit(), metricTemperature.getMeasurementUnit());


        when(departmentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(departmentAlpha));

        Department fetchedDepartment = departmentService.getDepartmentById(ID_ONE);

        assertEquals(fetchedDepartment.getId(), departmentAlpha.getId());
        assertEquals(fetchedDepartment.getName(), departmentAlpha.getName());


        int minutesToAdd = 10;
        double measurementValue = 30.5;
        int requestedPage = 1;
        int requestedSize = 7;
        int total = 62;

        List<Measurement> measurementList = populateMeasurementList(requestedSize, minutesToAdd, measurementValue);
        PageRequest pageRequest = PageRequest.of(requestedPage, requestedSize);
        Page<Measurement> measurementPage = new PageImpl<>(measurementList, pageRequest, total);

        when(measurementRepository.findMeasurementsByMetricAndDepartmentAndMeasurementTimestampBetween(any(Pageable.class),
                any(Metric.class), any(Department.class), any(ZonedDateTime.class), any(ZonedDateTime.class))).thenReturn(measurementPage);

        Page<Measurement> fetchedMeasurementPage =
                measurementRepository.findMeasurementsByMetricAndDepartmentAndMeasurementTimestampBetween(pageRequest,
                        fetchedMetric, fetchedDepartment, requestDateTime.truncatedTo(ChronoUnit.DAYS), requestDateTime);

        assertEquals(measurementPage.getTotalPages(), fetchedMeasurementPage.getTotalPages());
        assertEquals(measurementPage.getNumber(), fetchedMeasurementPage.getNumber());
        assertEquals(measurementPage.getContent().size(), fetchedMeasurementPage.getContent().size());
        assertEquals(measurementPage.getContent().get(1).getValue(), fetchedMeasurementPage.getContent().get(1).getValue());
        assertEquals(measurementPage.getContent().get(1).getMeasurementTimestamp().getMinute(),
                fetchedMeasurementPage.getContent().get(1).getMeasurementTimestamp().getMinute());
        assertEquals(measurementPage.getContent().get(1).getMetric().getMeasurementUnit(), fetchedMeasurementPage.getContent().get(1).getMetric().getMeasurementUnit());
    }

    @Test
    void getDailyMeasurementsByMetricAndDepartmentNotFoundMetric() {

        when(metricRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class,
                        () -> measurementService.getDailyMeasurementsByMetricAndDepartment(
                                PageRequest.of(0, 10),
                                metricTemperature.getId(),
                                departmentAlpha.getId()));

        assertNotNull(exception);
        assertEquals(NO_MEASUREMENT_RECORDS_FOUND, exception.getMessage());
    }

    @Test
    void getDailyMeasurementsByMetricAndDepartmentNotFoundDepartment() {

        when(metricRepository.findById(anyLong())).thenReturn(Optional.ofNullable(metricTemperature));

        Metric fetchedMetric = metricService.getMetricById(ID_ONE);

        assertEquals(fetchedMetric.getId(), metricTemperature.getId());
        assertEquals(fetchedMetric.getType(), metricTemperature.getType());
        assertEquals(fetchedMetric.getMeasurementUnit(), metricTemperature.getMeasurementUnit());


        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class,
                        () -> measurementService.getDailyMeasurementsByMetricAndDepartment(
                                PageRequest.of(0, 10),
                                metricTemperature.getId(),
                                departmentAlpha.getId()));

        assertNotNull(exception);
        assertEquals(NO_MEASUREMENT_RECORDS_FOUND, exception.getMessage());
    }

    @Test
    void getDailyAggregatedResults() {

        AggregatedResult dailyAggregatedResult = AggregatedResult.builder()
                .averageValue(23.55)
                .minValue(17.4)
                .maxValue(25.1)
                .build();

        when(measurementRepository.getAggregatedResults(anyLong(), anyLong(),
                any(ZonedDateTime.class), any(ZonedDateTime.class))).thenReturn(dailyAggregatedResult);

        ZonedDateTime startOfDay = ZonedDateTime.of(requestedDate.atTime(LocalTime.MIN), ZoneId.systemDefault());
        ZonedDateTime endOfDay = startOfDay.with(LocalTime.MAX);

        AggregatedResult fetchedAggregatedResult = measurementRepository.getAggregatedResults(ID_ONE, ID_ONE, startOfDay, endOfDay);

        assertEquals(fetchedAggregatedResult.getAverageValue(), dailyAggregatedResult.getAverageValue());
        assertEquals(fetchedAggregatedResult.getMinValue(), dailyAggregatedResult.getMinValue());
        assertEquals(fetchedAggregatedResult.getMaxValue(), dailyAggregatedResult.getMaxValue());
    }

    @Test
    void getWeeklyAggregatedResults() {

        AggregatedResult weeklyAggregatedResult = AggregatedResult.builder()
                .averageValue(22.15)
                .minValue(16.9)
                .maxValue(26.7)
                .build();

        when(measurementRepository.getAggregatedResults(anyLong(), anyLong(),
                any(ZonedDateTime.class), any(ZonedDateTime.class))).thenReturn(weeklyAggregatedResult);

        ZonedDateTime startOfWeek = ZonedDateTime.of(requestedDate.with(WeekFields.ISO.getFirstDayOfWeek()).atTime(LocalTime.MIN), ZoneId.systemDefault());
        ZonedDateTime endOfWeek = startOfWeek.plusDays(6).with(LocalTime.MAX);

        AggregatedResult fetchedAggregatedResult = measurementRepository.getAggregatedResults(ID_ONE, ID_ONE, startOfWeek, endOfWeek);

        assertEquals(fetchedAggregatedResult.getAverageValue(), weeklyAggregatedResult.getAverageValue());
        assertEquals(fetchedAggregatedResult.getMinValue(), weeklyAggregatedResult.getMinValue());
        assertEquals(fetchedAggregatedResult.getMaxValue(), weeklyAggregatedResult.getMaxValue());
    }

    private List<Measurement> populateMeasurementList(int requestedSize, int minutesToAdd, double measurementValue) {

        List<Measurement> populatedList = new ArrayList<>();

        for (int i = 0; i < requestedSize; i++) {
            populatedList.add(Measurement
                    .builder()
                    .value(measurementValue + i)
                    .measurementTimestamp(measurementDateTime.plusMinutes(minutesToAdd))
                    .metric(metricTemperature)
                    .department(departmentAlpha)
                    .build());
        }

        return populatedList;
    }
}
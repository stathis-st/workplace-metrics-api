package com.stathis.workplacemetricsapi.services;

import com.stathis.workplacemetricsapi.domain.Metric;
import com.stathis.workplacemetricsapi.exception.ResourceConstraintViolationException;
import com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException;
import com.stathis.workplacemetricsapi.exception.ResourceNotFoundException;
import com.stathis.workplacemetricsapi.exception.ResourceNotUpdatedException;
import com.stathis.workplacemetricsapi.repositories.MetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.stathis.workplacemetricsapi.domain.BaseEntity.ID_ONE;
import static com.stathis.workplacemetricsapi.domain.Metric.CELSIUS;
import static com.stathis.workplacemetricsapi.domain.Metric.TEMPERATURE;
import static com.stathis.workplacemetricsapi.exception.ResourceConstraintViolationException.SAVE_RESOURCE_CONSTRAINT_VIOLATION;
import static com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException.RESOURCE_COULD_NOT_BE_DELETED;
import static com.stathis.workplacemetricsapi.exception.ResourceNotFoundException.RESOURCE_NOT_FOUND_WITH_ID;
import static com.stathis.workplacemetricsapi.exception.ResourceNotUpdatedException.RESOURCE_COULD_NOT_BE_UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MetricServiceImplTest {

    @Mock
    MetricRepository metricRepository;

    MetricService metricService;

    Metric metricTemperature;
    Metric metricForSave;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        metricService = new MetricServiceImpl(metricRepository);

        metricTemperature = Metric.builder().build();
        metricTemperature.setId(ID_ONE);
        metricTemperature.setType(TEMPERATURE);
        metricTemperature.setMeasurementUnit(CELSIUS);

        metricForSave = Metric.builder().build();
        metricForSave.setType(TEMPERATURE);
        metricForSave.setMeasurementUnit(CELSIUS);
    }

    @Test
    void getAllMetrics() {
        List<Metric> metricList = new ArrayList<>();

        char measurementUnit = 'A';
        StringBuilder type = new StringBuilder("A");
        for (int i = 0; i < 7; i++) {
            measurementUnit++;
            metricList.add(Metric.builder()
                    .type(type.append(measurementUnit).toString())
                    .measurementUnit(Character.toString(measurementUnit))
                    .build());
        }

        int requestedPage = 1;
        int requestedSize = 7;
        int total = 62;
        Page<Metric> metricPage = new PageImpl<>(metricList, PageRequest.of(requestedPage, requestedSize), total);

        when(metricRepository.findAll(any(Pageable.class))).thenReturn(metricPage);

        int expectedTotalPages;
        if (total % requestedSize == 0) {
            expectedTotalPages = total / requestedSize;
        } else {
            expectedTotalPages = (total / requestedSize) + 1;
        }
        assertEquals(expectedTotalPages, metricPage.getTotalPages());
        assertEquals(requestedPage, metricPage.getNumber());
        assertEquals(requestedSize, metricPage.getContent().size());
        assertEquals("ABC", metricPage.getContent().get(1).getType());
        assertEquals("C", metricPage.getContent().get(1).getMeasurementUnit());
    }

    @Test
    void getMetricById() {
        when(metricRepository.findById(anyLong())).thenReturn(Optional.ofNullable(metricTemperature));

        Metric fetchedMetric = metricService.getMetricById(ID_ONE);

        assertEquals(ID_ONE, fetchedMetric.getId());
        assertEquals(TEMPERATURE, fetchedMetric.getType());
        assertEquals(CELSIUS, fetchedMetric.getMeasurementUnit());
    }

    @Test
    void getMetricByIdResourceNotFound() {

        when(metricRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> metricService.getMetricById(ID_ONE));

        String expectedMessage = RESOURCE_NOT_FOUND_WITH_ID + ID_ONE;

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void saveMetric() {

        when(metricRepository.save(any(Metric.class))).thenReturn(metricForSave);

        Metric savedMetric = metricService.saveMetric(metricForSave);

        assertEquals(metricForSave.getType(), savedMetric.getType());
        assertEquals(metricForSave.getMeasurementUnit(), savedMetric.getMeasurementUnit());
    }

    @Test
    void saveMetricConstraintViolation() {

        when(metricRepository.save(any(Metric.class)))
                .thenThrow(new ResourceConstraintViolationException(SAVE_RESOURCE_CONSTRAINT_VIOLATION));

        ResourceConstraintViolationException exception =
                assertThrows(ResourceConstraintViolationException.class, () -> metricService.saveMetric(metricForSave));

        assertNotNull(exception);
        assertEquals(SAVE_RESOURCE_CONSTRAINT_VIOLATION, exception.getMessage());
    }

    @Test
    void updateMetric() {

        when(metricRepository.findById(anyLong())).thenReturn(Optional.ofNullable(metricTemperature));

        Metric fetchedMetric = metricService.getMetricById(ID_ONE);

        assertEquals(ID_ONE, fetchedMetric.getId());
        assertEquals(TEMPERATURE, fetchedMetric.getType());
        assertEquals(CELSIUS, fetchedMetric.getMeasurementUnit());

        fetchedMetric.setMeasurementUnit("Fahrenheit");

        when(metricRepository.save(any(Metric.class))).thenReturn(fetchedMetric);

        Metric updatedMetric = metricService.saveMetric(fetchedMetric);

        assertEquals(fetchedMetric.getMeasurementUnit(), updatedMetric.getMeasurementUnit());
    }

    @Test
    void updateMetricResourceNotFound() {

        when(metricRepository.findById(anyLong())).thenReturn(Optional.empty());

        Metric metricForUpdate = Metric.builder().measurementUnit("updated_unit").build();

        ResourceNotUpdatedException exception = assertThrows(ResourceNotUpdatedException.class,
                () -> metricService.updateMetric(ID_ONE, metricForUpdate));

        String expectedMessage = RESOURCE_COULD_NOT_BE_UPDATED + RESOURCE_NOT_FOUND_WITH_ID + ID_ONE;

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void deleteMetricById() {

        metricRepository.deleteById(ID_ONE);

        verify(metricRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteMetricByIdResourceNotFound() {
        doThrow(EmptyResultDataAccessException.class).when(metricRepository).deleteById(anyLong());

        ResourceNotDeletedException exception = assertThrows(ResourceNotDeletedException.class, () -> metricService.deleteMetricById(ID_ONE));

        String expectedMessage = RESOURCE_COULD_NOT_BE_DELETED + ID_ONE;

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
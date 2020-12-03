package com.stathis.workplacemetricsapi.services;

import com.stathis.workplacemetricsapi.domain.Metric;
import com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException;
import com.stathis.workplacemetricsapi.exception.ResourceNotFoundException;
import com.stathis.workplacemetricsapi.exception.ResourceNotUpdatedException;
import com.stathis.workplacemetricsapi.model.ResponseEntityWrapper;
import com.stathis.workplacemetricsapi.repositories.MetricRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException.RESOURCE_COULD_NOT_BE_DELETED;
import static com.stathis.workplacemetricsapi.exception.ResourceNotFoundException.RESOURCE_NOT_FOUND_FOR_ID;
import static com.stathis.workplacemetricsapi.exception.ResourceNotUpdatedException.RESOURCE_COULD_NOT_BE_UPDATED;

@Service
@AllArgsConstructor
public class MetricServiceImpl implements MetricService {

    private final MetricRepository metricRepository;

    @Override
    public ResponseEntityWrapper<Metric> getAllMetrics(Pageable pageable) {
        Page<Metric> metricPage = metricRepository.findAll(pageable);
        return new ResponseEntityWrapper<>(metricPage.getContent(),
                metricPage.getNumber(),
                metricPage.getTotalElements(),
                metricPage.getTotalPages());
    }

    @Override
    public Metric getMetricById(Long id) {
        return metricRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_FOR_ID + id));
    }

    @Override
    public Metric saveMetric(Metric metric) {
        return metricRepository.save(metric);
    }

    @Override
    public Metric updateMetric(Long id, Metric metric) {
        Metric savedMetric;
        try {
            savedMetric = getMetricById(id);
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotUpdatedException(RESOURCE_COULD_NOT_BE_UPDATED + ex.getMessage());
        }
        savedMetric.setType(metric.getType());
        savedMetric.setMeasurementUnit(metric.getMeasurementUnit());
        return saveMetric(savedMetric);
    }

    @Override
    public void deleteMetricById(Long id) {
        try {
            metricRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResourceNotDeletedException(RESOURCE_COULD_NOT_BE_DELETED + id);
        }
    }
}

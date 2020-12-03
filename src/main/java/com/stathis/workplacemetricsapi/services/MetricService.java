package com.stathis.workplacemetricsapi.services;

import com.stathis.workplacemetricsapi.domain.Metric;
import com.stathis.workplacemetricsapi.model.ResponseEntityWrapper;
import org.springframework.data.domain.Pageable;

public interface MetricService {

    ResponseEntityWrapper<Metric> getAllMetrics(Pageable pageable);

    Metric getMetricById(Long id);

    Metric saveMetric(Metric metric);

    Metric updateMetric(Long id, Metric metric);

    void deleteMetricById(Long id);
}

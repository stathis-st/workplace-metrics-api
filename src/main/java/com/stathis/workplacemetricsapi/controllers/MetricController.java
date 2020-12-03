package com.stathis.workplacemetricsapi.controllers;

import com.stathis.workplacemetricsapi.domain.Metric;
import com.stathis.workplacemetricsapi.model.ResponseEntityWrapper;
import com.stathis.workplacemetricsapi.services.MetricService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MetricController.BASE_URL)
@AllArgsConstructor
public class MetricController {

    public static final String BASE_URL = "/api/metrics";

    private final MetricService metricService;

    @GetMapping
    public ResponseEntityWrapper<Metric> getAllMetrics(@RequestParam(defaultValue = "0") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer size) {
        return metricService.getAllMetrics(PageRequest.of(page, size));
    }

    @GetMapping(("/{id}"))
    public Metric getMetricById(@PathVariable("id") Long id) {
        return metricService.getMetricById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Metric saveMetric(@RequestBody Metric metric) {
        return metricService.saveMetric(metric);
    }

    @PutMapping("/{id}")
    public Metric updateMetric(@PathVariable("id") Long id, @RequestBody Metric metric) {
        return metricService.updateMetric(id, metric);
    }

    @DeleteMapping("/{id}")
    public void deleteMetric(@PathVariable("id") Long id) {
        metricService.deleteMetricById(id);
    }
}

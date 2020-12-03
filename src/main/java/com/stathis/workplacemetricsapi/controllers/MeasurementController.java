package com.stathis.workplacemetricsapi.controllers;

import com.stathis.workplacemetricsapi.domain.Measurement;
import com.stathis.workplacemetricsapi.model.MeasurementDTO;
import com.stathis.workplacemetricsapi.model.ResponseEntityWrapper;
import com.stathis.workplacemetricsapi.services.MeasurementService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MeasurementController.BASE_URL)
@AllArgsConstructor
public class MeasurementController {

    public static final String BASE_URL = "/api/measurements";

    private final MeasurementService measurementService;

    @GetMapping
    public ResponseEntityWrapper<Measurement> getAllMeasurements(@RequestParam(defaultValue = "0") Integer page,
                                                                 @RequestParam(defaultValue = "10") Integer size) {
        return measurementService.getAllMeasurements(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Measurement getMeasurementById(@PathVariable("id") Long id) {
        return measurementService.getMeasurementById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Measurement saveMeasurement(@RequestBody MeasurementDTO measurementDTO) {
        return measurementService.saveMeasurement(measurementDTO);
    }

    @GetMapping("/daily")
    public ResponseEntityWrapper<Measurement> getMeasurementsByMetricIdAndDepartmentId(@RequestParam(defaultValue = "0") Integer page,
                                                                                       @RequestParam(defaultValue = "10") Integer size,
                                                                                       @RequestParam(required = false) Long metricId,
                                                                                       @RequestParam(required = false) Long departmentId) {
        return measurementService.getDailyMeasurements(PageRequest.of(page, size), metricId, departmentId);
    }
}

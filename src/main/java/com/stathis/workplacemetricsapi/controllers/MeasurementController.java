package com.stathis.workplacemetricsapi.controllers;

import com.stathis.workplacemetricsapi.domain.Measurement;
import com.stathis.workplacemetricsapi.model.AggregatedResult;
import com.stathis.workplacemetricsapi.model.MeasurementDTO;
import com.stathis.workplacemetricsapi.model.ResponseEntityWrapper;
import com.stathis.workplacemetricsapi.services.MeasurementService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(MeasurementController.BASE_URL)
@AllArgsConstructor
public class MeasurementController {

    public static final String BASE_URL = "/api/measurements";

    private final MeasurementService measurementService;

    @GetMapping
    public ResponseEntityWrapper<Measurement> getAllMeasurements(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                 @RequestParam(name = "size", defaultValue = "10") Integer size) {
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
    public ResponseEntityWrapper<Measurement> getDailyMeasurementsByMetricAndDepartment(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                                        @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                                                        @RequestParam(name = "metricId", required = false) Long metricId,
                                                                                        @RequestParam(name = "departmentId", required = false) Long departmentId) {
        return measurementService.getDailyMeasurementsByMetricAndDepartment(PageRequest.of(page, size), metricId, departmentId);
    }

    @GetMapping("/aggregated/daily")
    public AggregatedResult getDailyAggregatedResults(@RequestParam("metricId") Long metricId,
                                                      @RequestParam("departmentId") Long departmentId,
                                                      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate requestedDate) {
        return measurementService.getDailyAggregatedResults(metricId, departmentId, requestedDate);
    }

    @GetMapping("/aggregated/weekly")
    public AggregatedResult getWeeklyAggregatedResults(@RequestParam("metricId") Long metricId,
                                                       @RequestParam("departmentId") Long departmentId,
                                                       @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate requestedDate) {
        return measurementService.getWeeklyAggregatedResults(metricId, departmentId, requestedDate);
    }
}

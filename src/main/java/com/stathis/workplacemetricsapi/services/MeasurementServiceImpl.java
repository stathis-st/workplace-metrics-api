package com.stathis.workplacemetricsapi.services;

import com.stathis.workplacemetricsapi.domain.Measurement;
import com.stathis.workplacemetricsapi.exception.ResourceNotFoundException;
import com.stathis.workplacemetricsapi.model.ResponseEntityWrapper;
import com.stathis.workplacemetricsapi.repositories.MeasurementRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.stathis.workplacemetricsapi.exception.ResourceNotFoundException.RESOURCE_NOT_FOUND_FOR_ID;

@Service
@AllArgsConstructor
public class MeasurementServiceImpl implements MeasurementService {

    private final MeasurementRepository measurementRepository;

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
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_FOR_ID + id));
    }

    @Override
    public Measurement saveMeasurement(Measurement measurement) {
        return measurementRepository.save(measurement);
    }
}

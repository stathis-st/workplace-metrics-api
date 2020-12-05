package com.stathis.workplacemetricsapi.controllers;

import com.stathis.workplacemetricsapi.domain.Metric;
import com.stathis.workplacemetricsapi.exception.ResourceConstraintViolationException;
import com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException;
import com.stathis.workplacemetricsapi.exception.ResourceNotFoundException;
import com.stathis.workplacemetricsapi.exception.ResourceNotUpdatedException;
import com.stathis.workplacemetricsapi.model.ResponseEntityWrapper;
import com.stathis.workplacemetricsapi.services.MetricService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.stathis.workplacemetricsapi.domain.BaseEntity.ID_ONE;
import static com.stathis.workplacemetricsapi.domain.Metric.CELSIUS;
import static com.stathis.workplacemetricsapi.domain.Metric.TEMPERATURE;
import static com.stathis.workplacemetricsapi.exception.ResourceConstraintViolationException.SAVE_RESOURCE_CONSTRAINT_VIOLATION;
import static com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException.RESOURCE_COULD_NOT_BE_DELETED;
import static com.stathis.workplacemetricsapi.exception.ResourceNotFoundException.RESOURCE_NOT_FOUND_WITH_ID;
import static com.stathis.workplacemetricsapi.exception.ResourceNotUpdatedException.RESOURCE_COULD_NOT_BE_UPDATED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MetricControllerTest extends AbstractRestControllerTest {

    @Mock
    MetricService metricService;

    @InjectMocks
    MetricController metricController;

    MockMvc mockMvc;

    Metric metricTemperature;
    Metric metricForUpdate;
    Metric metricForSave;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(metricController)
                .setControllerAdvice(new RestResponseEntityExceptionHandler())
                .build();

        metricTemperature = Metric.builder().build();
        metricTemperature.setId(ID_ONE);
        metricTemperature.setType(TEMPERATURE);
        metricTemperature.setMeasurementUnit(CELSIUS);

        metricForUpdate = Metric.builder().build();
        metricForUpdate.setType(TEMPERATURE);
        metricForUpdate.setMeasurementUnit(CELSIUS);

        metricForSave = Metric.builder().build();
        metricForSave.setType(TEMPERATURE);
        metricForSave.setMeasurementUnit(CELSIUS);
    }

    @Test
    void getAllMetrics() throws Exception {
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

        int currentPage = 1;
        int totalItems = 62;
        int totalPages = 9;
        ResponseEntityWrapper<Metric> metricResponseEntityWrapper =
                new ResponseEntityWrapper<>(metricList, currentPage, (long) totalItems, totalPages);

        when(metricService.getAllMetrics(any(Pageable.class))).thenReturn(metricResponseEntityWrapper);

        mockMvc.perform(get(MetricController.BASE_URL)
                .param("name", "1")
                .param("size", "7")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entityList", hasSize(7)))
                .andExpect(jsonPath("$.currentPage", equalTo(currentPage)))
                .andExpect(jsonPath("$.totalItems", equalTo(totalItems)))
                .andExpect(jsonPath("$.totalPages", equalTo(totalPages)))
                .andExpect(jsonPath("$.entityList[1].type", equalTo("ABC")))
                .andExpect(jsonPath("$.entityList[1].measurementUnit", equalTo("C")));
    }

    @Test
    void getMetricById() throws Exception {

        when(metricService.getMetricById(anyLong())).thenReturn(metricTemperature);

        mockMvc.perform(get(MetricController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.type", equalTo(TEMPERATURE)))
                .andExpect(jsonPath("$.measurementUnit", equalTo(CELSIUS)));
    }

    @Test
    void getMetricByIdResourceNotFound() throws Exception {

        when(metricService.getMetricById(anyLong())).thenThrow(new ResourceNotFoundException(RESOURCE_NOT_FOUND_WITH_ID + 555));

        mockMvc.perform(get(MetricController.BASE_URL + "/555")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveMetric() throws Exception {

        when(metricService.saveMetric(any(Metric.class))).thenReturn(metricTemperature);

        mockMvc.perform(post(MetricController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(metricForSave)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.type", equalTo(metricForSave.getType())))
                .andExpect(jsonPath("$.measurementUnit", equalTo(metricForSave.getMeasurementUnit())));
    }

    @Test
    void saveMetricConstraintViolation() throws Exception {

        when(metricService.saveMetric(any(Metric.class)))
                .thenThrow(new ResourceConstraintViolationException(SAVE_RESOURCE_CONSTRAINT_VIOLATION));

        mockMvc.perform(post(MetricController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(metricForSave)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMetric() throws Exception {

        Metric updatedMetric = Metric.builder().build();
        updatedMetric.setId(ID_ONE);
        updatedMetric.setMeasurementUnit("Fahrenheit");

        when(metricService.updateMetric(anyLong(), any(Metric.class))).thenReturn(updatedMetric);

        mockMvc.perform(put(MetricController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(metricForUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.measurementUnit", equalTo("Fahrenheit")));
    }

    @Test
    void updateMetricResourceNotFound() throws Exception {

        when(metricService.updateMetric(anyLong(), any(Metric.class)))
                .thenThrow(new ResourceNotUpdatedException(RESOURCE_COULD_NOT_BE_UPDATED + RESOURCE_NOT_FOUND_WITH_ID + 555));

        mockMvc.perform(put(MetricController.BASE_URL + "/555")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(metricForUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMetricConstraintViolation() throws Exception {
        when(metricService.updateMetric(anyLong(), any(Metric.class)))
                .thenThrow(new ResourceConstraintViolationException(SAVE_RESOURCE_CONSTRAINT_VIOLATION));

        mockMvc.perform(put(MetricController.BASE_URL + "/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(metricForUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMetric() throws Exception {

        mockMvc.perform(delete(MetricController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(metricService).deleteMetricById(anyLong());
    }

    @Test
    void deleteMetricResourceNotFound() throws Exception {

        doThrow(new ResourceNotDeletedException(RESOURCE_COULD_NOT_BE_DELETED + "/555"))
                .when(metricService).deleteMetricById(555L);

        mockMvc.perform(delete(MetricController.BASE_URL + "/555")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
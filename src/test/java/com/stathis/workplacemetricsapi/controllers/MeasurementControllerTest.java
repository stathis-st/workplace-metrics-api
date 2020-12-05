package com.stathis.workplacemetricsapi.controllers;

import com.stathis.workplacemetricsapi.domain.Department;
import com.stathis.workplacemetricsapi.domain.Measurement;
import com.stathis.workplacemetricsapi.domain.Metric;
import com.stathis.workplacemetricsapi.exception.ResourceNotFoundException;
import com.stathis.workplacemetricsapi.model.MeasurementDTO;
import com.stathis.workplacemetricsapi.model.ResponseEntityWrapper;
import com.stathis.workplacemetricsapi.services.MeasurementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.stathis.workplacemetricsapi.domain.BaseEntity.ID_ONE;
import static com.stathis.workplacemetricsapi.domain.Department.ALPHA;
import static com.stathis.workplacemetricsapi.domain.Measurement.VALUE_20;
import static com.stathis.workplacemetricsapi.domain.Metric.CELSIUS;
import static com.stathis.workplacemetricsapi.domain.Metric.TEMPERATURE;
import static com.stathis.workplacemetricsapi.exception.ResourceNotFoundException.RESOURCE_NOT_FOUND_WITH_ID;
import static com.stathis.workplacemetricsapi.services.MeasurementServiceImpl.DEPARTMENT_NOT_FOUND_WITH_ID;
import static com.stathis.workplacemetricsapi.services.MeasurementServiceImpl.FAILED_TO_SAVE_MEASUREMENT_RECORD;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MeasurementControllerTest extends AbstractRestControllerTest {

    @Mock
    MeasurementService measurementService;

    @InjectMocks
    MeasurementController measurementController;

    MockMvc mockMvc;

    Measurement measurementAlphaTemperature;
    MeasurementDTO measurementDTOForSave;

    ZonedDateTime measurementDateTime;

    Department departmentAlpha;
    Metric metricTemperature;

    LocalDate requestedDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(measurementController)
                .setControllerAdvice(new RestResponseEntityExceptionHandler())
                .build();

        metricTemperature = Metric.builder().build();
        metricTemperature.setId(ID_ONE);
        metricTemperature.setType(TEMPERATURE);
        metricTemperature.setMeasurementUnit(CELSIUS);

        departmentAlpha = Department.builder().build();
        departmentAlpha.setId(ID_ONE);
        departmentAlpha.setName(ALPHA);

        measurementDateTime = ZonedDateTime.of(LocalDate.of(2020, 12, 2),
                LocalTime.of(20, 20, 20), ZoneId.systemDefault());

        measurementDTOForSave = MeasurementDTO.builder()
                .value(VALUE_20)
                .measurementTimestamp(measurementDateTime)
                .metricId(metricTemperature.getId())
                .departmentId(departmentAlpha.getId())
                .build();

        measurementAlphaTemperature = Measurement.builder().build();
        measurementAlphaTemperature.setId(ID_ONE);
        measurementAlphaTemperature.setValue(VALUE_20);
        measurementAlphaTemperature.setMeasurementTimestamp(measurementDateTime);
        measurementAlphaTemperature.setDepartment(departmentAlpha);
        measurementAlphaTemperature.setMetric(metricTemperature);

        requestedDate = LocalDate.of(2020, 12, 2);
    }

    @Test
    void getAllMeasurements() throws Exception {

        int minutesToAdd = 10;
        double measurementValue = 30.5;
        int requestedPage = 1;
        int requestedSize = 7;
        int totalItems = 62;
        int totalPages = 9;

        List<Measurement> measurementList = populateMeasurementList(requestedSize, minutesToAdd, measurementValue);

        ResponseEntityWrapper<Measurement> measurementResponseEntityWrapper =
                new ResponseEntityWrapper<>(measurementList, requestedPage, (long) totalItems, totalPages);

        when(measurementService.getAllMeasurements(any(Pageable.class))).thenReturn(measurementResponseEntityWrapper);

        mockMvc.perform(get(MeasurementController.BASE_URL)
                .param("name", "1")
                .param("size", "7")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entityList", hasSize(requestedSize)))
                .andExpect(jsonPath("$.currentPage", equalTo(requestedPage)))
                .andExpect(jsonPath("$.totalItems", equalTo(totalItems)))
                .andExpect(jsonPath("$.totalPages", equalTo(totalPages)))
                .andExpect(jsonPath("$.entityList[1].value", equalTo(measurementList.get(1).getValue())))
                .andExpect(jsonPath("$.entityList[1].metric.id", equalTo(1)))
                .andExpect(jsonPath("$.entityList[1].metric.type", equalTo(measurementList.get(1).getMetric().getType())))
                .andExpect(jsonPath("$.entityList[1].metric.measurementUnit", equalTo(measurementList.get(1).getMetric().getMeasurementUnit())))
                .andExpect(jsonPath("$.entityList[1].department.id", equalTo(1)))
                .andExpect(jsonPath("$.entityList[1].department.name", equalTo(measurementList.get(1).getDepartment().getName())));
    }

    @Test
    void getMeasurementById() throws Exception {

        when(measurementService.getMeasurementById(anyLong())).thenReturn(measurementAlphaTemperature);

        mockMvc.perform(get(MeasurementController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.value", equalTo(measurementAlphaTemperature.getValue())))
                .andExpect(jsonPath("$.metric.id", equalTo(1)))
                .andExpect(jsonPath("$.metric.type", equalTo(measurementAlphaTemperature.getMetric().getType())))
                .andExpect(jsonPath("$.metric.measurementUnit", equalTo(measurementAlphaTemperature.getMetric().getMeasurementUnit())))
                .andExpect(jsonPath("$.department.id", equalTo(1)))
                .andExpect(jsonPath("$.department.name", equalTo(measurementAlphaTemperature.getDepartment().getName())));
    }

    @Test
    void getMeasurementByIdResourceNotFound() throws Exception {

        when(measurementService.getMeasurementById(anyLong())).thenThrow(new ResourceNotFoundException(RESOURCE_NOT_FOUND_WITH_ID + 555));

        mockMvc.perform(get(MeasurementController.BASE_URL + "/555")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Disabled
    void saveMeasurement() throws Exception {

        when(measurementService.saveMeasurement(any(MeasurementDTO.class))).thenReturn(measurementAlphaTemperature);

        mockMvc.perform(post(MeasurementController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(measurementDTOForSave)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.value", equalTo(measurementAlphaTemperature.getValue())))
                .andExpect(jsonPath("$.metric.id", equalTo(1)))
                .andExpect(jsonPath("$.metric.type", equalTo(measurementAlphaTemperature.getMetric().getType())))
                .andExpect(jsonPath("$.metric.measurementUnit", equalTo(measurementAlphaTemperature.getMetric().getMeasurementUnit())))
                .andExpect(jsonPath("$.department.id", equalTo(1)))
                .andExpect(jsonPath("$.department.name", equalTo(measurementAlphaTemperature.getDepartment().getName())));

    }

    @Test
    @Disabled
    void saveMeasurementDepartmentNotFound() throws Exception {

        when(measurementService.saveMeasurement(any(MeasurementDTO.class)))
                .thenThrow(new ResourceNotFoundException(FAILED_TO_SAVE_MEASUREMENT_RECORD + DEPARTMENT_NOT_FOUND_WITH_ID + 1));

        mockMvc.perform(post(MetricController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(measurementDTOForSave)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDailyMeasurementsByMetricAndDepartment() {
    }

    @Test
    void getDailyAggregatedResults() {
    }

    @Test
    void getWeeklyAggregatedResults() {
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
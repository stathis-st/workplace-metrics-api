package com.stathis.workplacemetricsapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeasurementDTO {

    private Double value;
    private ZonedDateTime measurementTimestamp;

    @JsonProperty("metric_id")
    private Long metricId;

    @JsonProperty("department_id")
    private Long departmentId;
}

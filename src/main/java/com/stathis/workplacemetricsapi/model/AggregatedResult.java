package com.stathis.workplacemetricsapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregatedResult {

    private Double averageValue;
    private Double minValue;
    private Double maxValue;
}

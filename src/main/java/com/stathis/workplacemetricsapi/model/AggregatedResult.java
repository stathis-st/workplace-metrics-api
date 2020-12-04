package com.stathis.workplacemetricsapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedResult {

    private Double averageValue;
    private Double minValue;
    private Double maxValue;
}

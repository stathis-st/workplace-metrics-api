package com.stathis.workplacemetricsapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedResults {

    private Long averageValue;
    private Long minValue;
    private Long maxValue;
}

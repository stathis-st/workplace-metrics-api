package com.stathis.workplacemetricsapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "measurements",
        indexes = @Index(columnList = "measurement_timestamp, metric_id, department_id"))
public class Measurement extends CreationBaseEntity {

    public static final Double VALUE_20 = 20.0;

    @Column(name = "value", nullable = false)
    private Double value;

    @Column(name = "measurement_timestamp", nullable = false)
    private ZonedDateTime measurementTimestamp;

    @ManyToOne
    @JoinColumn(name = "metric_id", nullable = false)
    private Metric metric;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}

package com.stathis.workplacemetricsapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "measurements")
public class Measurement extends CreationBaseEntity {

    @NotNull
    @Column(name = "value")
    private Double value;

    @NotNull
    @Column(name = "measurement_timestamp")
    private Timestamp measurementTimestamp;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "metric_id")
    private Metric metric;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}

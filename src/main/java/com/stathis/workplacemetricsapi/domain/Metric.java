package com.stathis.workplacemetricsapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "metrics")
public class Metric extends BaseEntity {

    @Column(name = "type", unique = true)
    private String type;

    @Column(name = "unit_of_measurement", unique = true)
    private String unitOfMeasurement;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "metric")
    private Set<Measurement> measurements = new HashSet<>();
}

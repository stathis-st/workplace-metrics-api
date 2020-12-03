package com.stathis.workplacemetricsapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Metric extends UpdateBaseEntity {

    @Column(name = "type", unique = true, nullable = false)
    private String type;

    @Column(name = "measurement_unit", unique = true, nullable = false)
    private String measurementUnit;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "metric")
    private Set<Measurement> measurements = new HashSet<>();
}

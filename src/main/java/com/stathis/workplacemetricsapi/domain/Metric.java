package com.stathis.workplacemetricsapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "metrics")
public class Metric extends UpdateBaseEntity {

    public static final String TEMPERATURE = "Temperature";
    public static final String CELSIUS = "Celsius";

    @Column(name = "type", unique = true, nullable = false)
    private String type;

    @Column(name = "measurement_unit", unique = true, nullable = false)
    private String measurementUnit;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "metric")
    private Set<Measurement> measurements = new HashSet<>();
}

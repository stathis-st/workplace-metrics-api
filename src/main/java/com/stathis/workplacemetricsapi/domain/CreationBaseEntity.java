package com.stathis.workplacemetricsapi.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class CreationBaseEntity extends BaseEntity {

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected ZonedDateTime createdTimestamp;
}

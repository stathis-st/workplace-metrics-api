package com.stathis.workplacemetricsapi.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class UpdateBaseEntity extends CreationBaseEntity {

    @UpdateTimestamp
    @Column(name = "updated", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected ZonedDateTime updatedTimestamp;
}

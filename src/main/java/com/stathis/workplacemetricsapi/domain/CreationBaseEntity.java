package com.stathis.workplacemetricsapi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class CreationBaseEntity extends BaseEntity {

    @NotNull
    @CreationTimestamp
    @Column(name = "created")
    protected ZonedDateTime createdTimestamp;
}

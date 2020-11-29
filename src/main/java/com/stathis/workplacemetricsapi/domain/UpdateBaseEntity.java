package com.stathis.workplacemetricsapi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class UpdateBaseEntity extends CreationBaseEntity {

    @NotNull
    @UpdateTimestamp
    @Column(name = "updated")
    protected Timestamp updatedTimestamp;
}

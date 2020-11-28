package com.stathis.workplacemetricsapi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class UpdateBaseEntity extends BaseEntity {

    @UpdateTimestamp
    @Column(name = "updated")
    protected Timestamp updatedTimeStamp;
}

package com.stathis.workplacemetricsapi.model;

import com.stathis.workplacemetricsapi.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEntityWrapper<T extends BaseEntity> {

    private List<T> entityList;
    private Integer currentPage;
    private Long totalItems;
    private Integer totalPages;

}

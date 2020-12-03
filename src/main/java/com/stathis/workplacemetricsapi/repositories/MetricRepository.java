package com.stathis.workplacemetricsapi.repositories;

import com.stathis.workplacemetricsapi.domain.Metric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricRepository extends JpaRepository<Metric, Long> {
}

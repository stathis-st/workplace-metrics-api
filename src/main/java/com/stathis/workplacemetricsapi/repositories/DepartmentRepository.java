package com.stathis.workplacemetricsapi.repositories;

import com.stathis.workplacemetricsapi.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}

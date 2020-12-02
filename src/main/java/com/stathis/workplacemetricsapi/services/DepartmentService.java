package com.stathis.workplacemetricsapi.services;

import com.stathis.workplacemetricsapi.domain.Department;
import com.stathis.workplacemetricsapi.model.ResponseEntityWrapper;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

    ResponseEntityWrapper<Department> getAllDepartments(Pageable pageable);

    Department getDepartmentById(Long id);

    Department saveDepartment(Department department);

    Department updateDepartment(Long id, Department department);

    void deleteDepartmentById(Long id);
}

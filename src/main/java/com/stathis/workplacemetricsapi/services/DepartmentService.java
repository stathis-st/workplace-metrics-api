package com.stathis.workplacemetricsapi.services;

import com.stathis.workplacemetricsapi.domain.Department;

import java.util.List;

public interface DepartmentService {

    List<Department> getAllDepartments();

    Department getDepartmentById(Long id);

    Department saveDepartment(Department department);

    void deleteDepartmentById(Long id);
}

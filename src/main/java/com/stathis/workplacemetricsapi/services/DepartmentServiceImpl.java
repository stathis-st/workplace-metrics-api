package com.stathis.workplacemetricsapi.services;

import com.stathis.workplacemetricsapi.domain.Department;
import com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException;
import com.stathis.workplacemetricsapi.exception.ResourceNotFoundException;
import com.stathis.workplacemetricsapi.exception.ResourceNotUpdatedException;
import com.stathis.workplacemetricsapi.repositories.DepartmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException.RESOURCE_COULD_NOT_BE_DELETED;
import static com.stathis.workplacemetricsapi.exception.ResourceNotFoundException.RESOURCE_NOT_FOUND_FOR_ID;
import static com.stathis.workplacemetricsapi.exception.ResourceNotUpdatedException.RESOURCE_COULD_NOT_BE_UPDATED;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_FOR_ID + id));
    }

    @Override
    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(Long id, Department department) {
        Department savedDepartment;
        try {
            savedDepartment = getDepartmentById(id);
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotUpdatedException(RESOURCE_COULD_NOT_BE_UPDATED + ex.getMessage());
        }
        savedDepartment.setName(department.getName());
        return saveDepartment(savedDepartment);
    }

    @Override
    public void deleteDepartmentById(Long id) {
        try {
            departmentRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResourceNotDeletedException(RESOURCE_COULD_NOT_BE_DELETED + id);
        }
    }
}

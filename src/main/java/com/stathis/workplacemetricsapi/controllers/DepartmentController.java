package com.stathis.workplacemetricsapi.controllers;

import com.stathis.workplacemetricsapi.domain.Department;
import com.stathis.workplacemetricsapi.services.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(DepartmentController.BASE_URL)
@AllArgsConstructor
public class DepartmentController {

    public static final String BASE_URL = "/api/departments";

    private final DepartmentService departmentService;

    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("/{id}")
    public Department getDepartmentById(@PathVariable("id") Long id) {
        return departmentService.getDepartmentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Department saveDepartment(@RequestBody Department department) {
        return departmentService.saveDepartment(department);
    }

    @PutMapping("/{id}")
    public Department updateDepartment(@PathVariable("id") Long id, @RequestBody Department department) {
        return departmentService.updateDepartment(id, department);
    }

    @DeleteMapping("/{id}")
    public void deleteDepartment(@PathVariable("id") Long id) {
        departmentService.deleteDepartmentById(id);
    }
}

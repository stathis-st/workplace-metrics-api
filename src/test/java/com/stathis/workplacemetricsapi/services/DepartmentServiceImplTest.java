package com.stathis.workplacemetricsapi.services;

import com.stathis.workplacemetricsapi.domain.Department;
import com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException;
import com.stathis.workplacemetricsapi.exception.ResourceNotFoundException;
import com.stathis.workplacemetricsapi.exception.ResourceNotUpdatedException;
import com.stathis.workplacemetricsapi.repositories.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException.RESOURCE_COULD_NOT_BE_DELETED;
import static com.stathis.workplacemetricsapi.exception.ResourceNotFoundException.RESOURCE_NOT_FOUND_FOR_ID;
import static com.stathis.workplacemetricsapi.exception.ResourceNotUpdatedException.RESOURCE_COULD_NOT_BE_UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DepartmentServiceImplTest {

    @Mock
    DepartmentRepository departmentRepository;

    DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        departmentService = new DepartmentServiceImpl(departmentRepository);
    }

    @Test
    void getAllDepartments() {
        //given
        Department alpha = Department.builder().build();
        alpha.setId(1L);
        alpha.setName("alpha");

        Department beta = Department.builder().build();
        beta.setId(2L);
        beta.setName("beta");

        when(departmentRepository.findAll()).thenReturn(Arrays.asList(alpha, beta));

        //when
        List<Department> departments = departmentService.getAllDepartments();

        //then
        assertEquals(2, departments.size());
        assertEquals(1L, departments.get(0).getId());
        assertEquals(2L, departments.get(1).getId());
        assertEquals("alpha", departments.get(0).getName());
        assertEquals("beta", departments.get(1).getName());
    }

    @Test
    void getDepartmentById() {
        //given
        Department alpha = Department.builder().build();
        alpha.setId(1L);
        alpha.setName("alpha");

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(alpha));

        //when
        Department department = departmentService.getDepartmentById(1L);

        //then
        assertEquals(1L, department.getId());
        assertEquals("alpha", department.getName());
    }

    @Test
    void getDepartmentByIdNotFound() {
        //given
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        Long id = 1L;
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> departmentService.getDepartmentById(id));

        String expectedMessage = RESOURCE_NOT_FOUND_FOR_ID + id;

        //then
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void saveDepartment() {

        //given
        Department department = Department.builder().build();
        department.setName("alpha");

        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        //when
        Department savedDepartment = departmentService.saveDepartment(department);

        assertEquals(department.getName(), savedDepartment.getName());
    }

    @Test
    void updateDepartment() {
        //given
        Department alpha = Department.builder().build();
        alpha.setId(1L);
        alpha.setName("alpha");

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(alpha));

        //when
        Department fetchedDepartment = departmentService.getDepartmentById(1L);

        //then
        assertEquals(1L, fetchedDepartment.getId());
        assertEquals("alpha", fetchedDepartment.getName());


        //given
        fetchedDepartment.setName("alpha_department");

        when(departmentRepository.save(any(Department.class))).thenReturn(fetchedDepartment);

        //when
        Department updatedDepartment = departmentService.saveDepartment(fetchedDepartment);

        //then
        assertEquals(fetchedDepartment.getName(), updatedDepartment.getName());

    }

    @Test
    void updateDepartmentResourceNotFound() {
        //given
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Long id = 1L;
        Department departmentForUpdate = Department.builder().name("updated_name").build();

        ResourceNotUpdatedException exception = assertThrows(ResourceNotUpdatedException.class,
                () -> departmentService.updateDepartment(id, departmentForUpdate));

        String expectedMessage = RESOURCE_COULD_NOT_BE_UPDATED + RESOURCE_NOT_FOUND_FOR_ID + id;

        //then
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void deleteDepartmentById() {

        Long id = 1L;

        departmentRepository.deleteById(id);

        verify(departmentRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteDepartmentByIdResourceNotFound() {

        doThrow(EmptyResultDataAccessException.class).when(departmentRepository).deleteById(anyLong());

        Long id = 1L;
        ResourceNotDeletedException exception = assertThrows(ResourceNotDeletedException.class, () -> departmentService.deleteDepartmentById(id));

        String expectedMessage = RESOURCE_COULD_NOT_BE_DELETED + id;

        //then
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    //TODO refactor department objects for testing
}
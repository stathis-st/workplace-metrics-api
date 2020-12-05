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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.stathis.workplacemetricsapi.domain.BaseEntity.ID_ONE;
import static com.stathis.workplacemetricsapi.domain.Department.ALPHA;
import static com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException.RESOURCE_COULD_NOT_BE_DELETED;
import static com.stathis.workplacemetricsapi.exception.ResourceNotFoundException.RESOURCE_NOT_FOUND_WITH_ID;
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

    Department departmentAlpha;
    Department departmentForSave;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        departmentService = new DepartmentServiceImpl(departmentRepository);

        departmentAlpha = Department.builder().build();
        departmentAlpha.setId(ID_ONE);
        departmentAlpha.setName(ALPHA);

        departmentForSave = Department.builder().build();
        departmentForSave.setName(ALPHA);
    }

    @Test
    void getAllDepartments() {
        List<Department> departmentList = new ArrayList<>();

        char name = 'A';
        for (int i = 0; i < 7; i++) {
            departmentList.add(Department.builder().name(Character.toString(name)).build());
            name++;
        }
        int requestedPage = 1;
        int requestedSize = 7;
        int total = 62;
        Page<Department> departmentPage = new PageImpl<>(departmentList, PageRequest.of(requestedPage, requestedSize), total);

        when(departmentRepository.findAll(any(Pageable.class))).thenReturn(departmentPage);

        int expectedTotalPages;
        if (total % requestedSize == 0) {
            expectedTotalPages = total / requestedSize;
        } else {
            expectedTotalPages = (total / requestedSize) + 1;
        }
        assertEquals(expectedTotalPages, departmentPage.getTotalPages());
        assertEquals(requestedPage, departmentPage.getNumber());
        assertEquals(requestedSize, departmentPage.getContent().size());
        assertEquals("B", departmentPage.getContent().get(1).getName());
    }

    @Test
    void getDepartmentById() {

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(departmentAlpha));

        Department fetchedDepartment = departmentService.getDepartmentById(ID_ONE);

        assertEquals(ID_ONE, fetchedDepartment.getId());
        assertEquals(ALPHA, fetchedDepartment.getName());
    }

    @Test
    void getDepartmentByIdResourceNotFound() {

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> departmentService.getDepartmentById(ID_ONE));

        String expectedMessage = RESOURCE_NOT_FOUND_WITH_ID + ID_ONE;

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void saveDepartment() {

        when(departmentRepository.save(any(Department.class))).thenReturn(departmentAlpha);

        Department savedDepartment = departmentService.saveDepartment(departmentForSave);

        assertEquals(departmentForSave.getName(), savedDepartment.getName());
    }

    @Test
    void updateDepartment() {

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(departmentAlpha));

        Department fetchedDepartment = departmentService.getDepartmentById(ID_ONE);

        assertEquals(ID_ONE, fetchedDepartment.getId());
        assertEquals(ALPHA, fetchedDepartment.getName());


        fetchedDepartment.setName("alpha_department");

        when(departmentRepository.save(any(Department.class))).thenReturn(fetchedDepartment);

        Department updatedDepartment = departmentService.saveDepartment(fetchedDepartment);

        assertEquals(fetchedDepartment.getName(), updatedDepartment.getName());
    }

    @Test
    void updateDepartmentResourceNotFound() {

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Department departmentForUpdate = Department.builder().name("updated_name").build();

        ResourceNotUpdatedException exception = assertThrows(ResourceNotUpdatedException.class,
                () -> departmentService.updateDepartment(ID_ONE, departmentForUpdate));

        String expectedMessage = RESOURCE_COULD_NOT_BE_UPDATED + RESOURCE_NOT_FOUND_WITH_ID + ID_ONE;

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void deleteDepartmentById() {

        departmentRepository.deleteById(ID_ONE);

        verify(departmentRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteDepartmentByIdResourceNotFound() {

        doThrow(EmptyResultDataAccessException.class).when(departmentRepository).deleteById(anyLong());

        ResourceNotDeletedException exception = assertThrows(ResourceNotDeletedException.class, () -> departmentService.deleteDepartmentById(ID_ONE));

        String expectedMessage = RESOURCE_COULD_NOT_BE_DELETED + ID_ONE;

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
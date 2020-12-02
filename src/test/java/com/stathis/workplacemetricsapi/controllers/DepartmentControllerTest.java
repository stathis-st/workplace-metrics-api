package com.stathis.workplacemetricsapi.controllers;

import com.stathis.workplacemetricsapi.domain.Department;
import com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException;
import com.stathis.workplacemetricsapi.exception.ResourceNotFoundException;
import com.stathis.workplacemetricsapi.exception.ResourceNotUpdatedException;
import com.stathis.workplacemetricsapi.model.ResponseEntityWrapper;
import com.stathis.workplacemetricsapi.services.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException.RESOURCE_COULD_NOT_BE_DELETED;
import static com.stathis.workplacemetricsapi.exception.ResourceNotFoundException.RESOURCE_NOT_FOUND_FOR_ID;
import static com.stathis.workplacemetricsapi.exception.ResourceNotUpdatedException.RESOURCE_COULD_NOT_BE_UPDATED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DepartmentControllerTest extends AbstractRestControllerTest {

    @Mock
    DepartmentService departmentService;

    @InjectMocks
    DepartmentController departmentController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(departmentController)
                .setControllerAdvice(new RestResponseEntityExceptionHandler())
                .build();
    }

    @Test
    void getAllDepartments() throws Exception {
        List<Department> departmentList = new ArrayList<>();

        char name = 'A';
        for (int i = 0; i < 7; i++) {
            departmentList.add(Department.builder().name(Character.toString(name)).build());
            name++;
        }

        int currentPage = 1;
        int totalItems = 62;
        int totalPages = 9;
        ResponseEntityWrapper<Department> departmentResponseEntityWrapper =
                new ResponseEntityWrapper<>(departmentList, currentPage, (long) totalItems, totalPages);

        when(departmentService.getAllDepartments(any(Pageable.class))).thenReturn(departmentResponseEntityWrapper);

        mockMvc.perform(get(DepartmentController.BASE_URL)
                .param("name", "1")
                .param("size", "7")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entityList", hasSize(7)))
                .andExpect(jsonPath("$.currentPage", equalTo(currentPage)))
                .andExpect(jsonPath("$.totalItems", equalTo(totalItems)))
                .andExpect(jsonPath("$.totalPages", equalTo(totalPages)))
                .andExpect(jsonPath("$.entityList[1].name", equalTo("B")));
    }

    @Test
    void getDepartmentById() throws Exception {

        Department alpha = Department.builder().build();
        alpha.setId(1L);
        alpha.setName("alpha");

        when(departmentService.getDepartmentById(anyLong())).thenReturn(alpha);

        mockMvc.perform(get(DepartmentController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("alpha")));
    }

    @Test
    void getDepartmentByIdNotFound() throws Exception {

        when(departmentService.getDepartmentById(anyLong())).thenThrow(new ResourceNotFoundException(RESOURCE_NOT_FOUND_FOR_ID + 555));

        mockMvc.perform(get(DepartmentController.BASE_URL + "/555")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveDepartment() throws Exception {
        Department alpha = Department.builder().build();
        alpha.setId(1L);
        alpha.setName("alpha");

        when(departmentService.saveDepartment(any(Department.class))).thenReturn(alpha);

        mockMvc.perform(post(DepartmentController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alpha)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("alpha")));
    }

    @Test
    void updateDepartment() throws Exception {
        Department alpha = Department.builder().build();
        alpha.setName("alpha");

        Department updatedDepartment = Department.builder().build();
        updatedDepartment.setId(1L);
        updatedDepartment.setName("alpha_department");

        when(departmentService.updateDepartment(anyLong(), any(Department.class))).thenReturn(updatedDepartment);

        mockMvc.perform(put(DepartmentController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alpha)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("alpha_department")));
    }

    @Test
    void updateDepartmentNotFound() throws Exception {
        Department alpha = Department.builder().build();
        alpha.setName("alpha");

        when(departmentService.updateDepartment(anyLong(), any(Department.class)))
                .thenThrow(new ResourceNotUpdatedException(RESOURCE_COULD_NOT_BE_UPDATED + RESOURCE_NOT_FOUND_FOR_ID + 555));

        mockMvc.perform(put(DepartmentController.BASE_URL + "/555")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alpha)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDepartment() throws Exception {

        mockMvc.perform(delete(DepartmentController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(departmentService).deleteDepartmentById(anyLong());
    }

    @Test
    void deleteDepartmentNotFound() throws Exception {

        doThrow(new ResourceNotDeletedException(RESOURCE_COULD_NOT_BE_DELETED + "/555"))
                .when(departmentService).deleteDepartmentById(555L);

        mockMvc.perform(delete(DepartmentController.BASE_URL + "/555")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    //TODO refactor department objects for testing
}
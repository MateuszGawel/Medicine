package com.gawel.medicine.controller;

import com.gawel.medicine.dto.DrugApplicationDto;
import com.gawel.medicine.service.DrugService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DrugController.class)
class DrugControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DrugService drugService;

    private DrugApplicationDto drugApplicationDto;

    @BeforeEach
    void setUp() {
        drugApplicationDto = new DrugApplicationDto();
        drugApplicationDto.setApplicationNumber("12345");
        drugApplicationDto.setManufacturerNames(Set.of("Test Manufacturer"));
        drugApplicationDto.setSubstanceNames(Set.of("Test Substance"));
        drugApplicationDto.setProductNumbers(Set.of("001", "002"));
    }

    @Test
    void testCreateDrugApplication() throws Exception {
        when(drugService.createDrugApplication(anyString())).thenReturn(drugApplicationDto);

        mockMvc.perform(post("/api/v1/drugs/application/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.applicationNumber", is("12345")))
                .andExpect(jsonPath("$.manufacturerNames", hasSize(1)))
                .andExpect(jsonPath("$.manufacturerNames[0]", is("Test Manufacturer")))
                .andExpect(jsonPath("$.substanceNames", hasSize(1)))
                .andExpect(jsonPath("$.substanceNames[0]", is("Test Substance")))
                .andExpect(jsonPath("$.productNumbers", hasSize(2)));
    }

    @Test
    void testGetAllDrugApplications() throws Exception {
        List<DrugApplicationDto> applications = Arrays.asList(drugApplicationDto);
        when(drugService.getAllDrugApplications()).thenReturn(applications);

        mockMvc.perform(get("/api/v1/drugs/applications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].applicationNumber", is("12345")))
                .andExpect(jsonPath("$[0].manufacturerNames", hasSize(1)))
                .andExpect(jsonPath("$[0].manufacturerNames[0]", is("Test Manufacturer")))
                .andExpect(jsonPath("$[0].substanceNames", hasSize(1)))
                .andExpect(jsonPath("$[0].substanceNames[0]", is("Test Substance")))
                .andExpect(jsonPath("$[0].productNumbers", hasSize(2)));
    }

    @Test
    void testCreateDrugApplicationNotFound() throws Exception {
        when(drugService.createDrugApplication(anyString())).thenThrow(new NoSuchElementException("Drug not found"));

        mockMvc.perform(post("/api/v1/drugs/application/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Drug Not Found")))
                .andExpect(jsonPath("$.message", is("Drug not found")))
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void testCreateDrugApplicationExternalApiError() throws Exception {
        when(drugService.createDrugApplication(anyString())).thenThrow(new RuntimeException("External API error"));

        mockMvc.perform(post("/api/v1/drugs/application/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.message", is("External API error")))
                .andExpect(jsonPath("$.status", is(500)));
    }
}

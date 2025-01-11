package com.gawel.medicine.controller;

import com.gawel.medicine.service.DrugService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FdaController.class)
class FdaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DrugService drugService;

    private Map<String, Object> drugSearchResponse;

    @BeforeEach
    void setUp() {
        drugSearchResponse = new HashMap<>();
        drugSearchResponse.put("total", 1);
        drugSearchResponse.put("drugs", List.of(Map.of("name", "Test Drug", "manufacturer", "Test Manufacturer")));
    }

    @Test
    void testSearchDrugs_success() throws Exception {
        when(drugService.fetchDrugs(anyString(), anyString(), anyInt(), anyInt())).thenReturn(drugSearchResponse);

        mockMvc.perform(get("/api/v1/fda/")
                        .param("manufacturerName", "Test Manufacturer")
                        .param("brandName", "Test Brand")
                        .param("page", "0")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.drugs", hasSize(1)))
                .andExpect(jsonPath("$.drugs[0].name", is("Test Drug")))
                .andExpect(jsonPath("$.drugs[0].manufacturer", is("Test Manufacturer")));
    }

    @Test
    void testSearchDrugs_missingManufacturerName() throws Exception {
        mockMvc.perform(get("/api/v1/fda/")
                        .param("brandName", "Test Brand")
                        .param("page", "0")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchDrugs_serviceThrowsException() throws Exception {
        when(drugService.fetchDrugs(anyString(), anyString(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Internal Server Error"));

        mockMvc.perform(get("/api/v1/fda/")
                        .param("manufacturerName", "Test Manufacturer")
                        .param("brandName", "Test Brand")
                        .param("page", "0")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.message", is("Internal Server Error")));
    }
}

package com.gawel.medicine.service;

import com.gawel.medicine.client.fda.FdaClient;
import com.gawel.medicine.client.fda.exception.FdaApiException;
import com.gawel.medicine.dto.DrugApplicationDto;
import com.gawel.medicine.mapper.DrugApplicationMapper;
import com.gawel.medicine.model.DrugApplicationRecord;
import com.gawel.medicine.repository.DrugApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DrugServiceTest {

    private FdaClient fdaClient;
    private DrugApplicationRepository drugApplicationRepository;
    private DrugApplicationMapper mapper;
    private DrugService drugService;

    private DrugApplicationDto drugApplicationDto;
    private DrugApplicationRecord drugApplicationRecord;

    @BeforeEach
    void setUp() {
        fdaClient = mock(FdaClient.class);
        drugApplicationRepository = mock(DrugApplicationRepository.class);
        mapper = mock(DrugApplicationMapper.class);
        drugService = new DrugService(fdaClient, drugApplicationRepository, mapper);

        drugApplicationDto = new DrugApplicationDto();
        drugApplicationDto.setApplicationNumber("12345");
        drugApplicationDto.setManufacturerNames(Set.of("Test Manufacturer"));
        drugApplicationDto.setSubstanceNames(Set.of("Test Substance"));
        drugApplicationDto.setProductNumbers(Set.of("001", "002"));

        drugApplicationRecord = new DrugApplicationRecord();
        drugApplicationRecord.setApplicationNumber("12345");
        drugApplicationRecord.setManufacturerNames(Set.of("Test Manufacturer"));
        drugApplicationRecord.setSubstanceNames(Set.of("Test Substance"));
        drugApplicationRecord.setProductNumbers(Set.of("001", "002"));
    }

    @Test
    void fetchDrugs_success() {
        Map<String, Object> mockResponse = Map.of("total", 1, "drugs", List.of(drugApplicationDto));
        when(fdaClient.fetchDrugs(anyString(), anyString(), anyInt(), anyInt())).thenReturn(mockResponse);

        Map<String, Object> response = drugService.fetchDrugs("Test Manufacturer", "Test Brand", 0, 10);

        assertNotNull(response);
        assertEquals(1, response.get("total"));
        assertTrue(((List<?>) response.get("drugs")).contains(drugApplicationDto));
        verify(fdaClient, times(1)).fetchDrugs("Test Manufacturer", "Test Brand", 0, 10);
    }

    @Test
    void fetchDrugs_emptyResponse() {
        when(fdaClient.fetchDrugs(anyString(), anyString(), anyInt(), anyInt())).thenReturn(Map.of());

        Map<String, Object> response = drugService.fetchDrugs("Unknown Manufacturer", "Unknown Brand", 0, 10);

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(fdaClient, times(1)).fetchDrugs("Unknown Manufacturer", "Unknown Brand", 0, 10);
    }

    @Test
    void fetchDrugs_fdaApiException() {
        when(fdaClient.fetchDrugs(anyString(), anyString(), anyInt(), anyInt()))
                .thenThrow(new FdaApiException("FDA API error occurred"));

        FdaApiException exception = assertThrows(FdaApiException.class, () ->
                drugService.fetchDrugs("Test Manufacturer", "Test Brand", 0, 10));

        assertEquals("FDA API error occurred", exception.getMessage());
        verify(fdaClient, times(1)).fetchDrugs("Test Manufacturer", "Test Brand", 0, 10);
    }

    @Test
    void createDrugApplication_success() {
        when(fdaClient.getDrugApplicationByNumber(anyString())).thenReturn(drugApplicationDto);
        when(mapper.toRecord(any(DrugApplicationDto.class))).thenReturn(drugApplicationRecord);
        when(drugApplicationRepository.save(any(DrugApplicationRecord.class))).thenReturn(drugApplicationRecord);
        when(mapper.toDto(any(DrugApplicationRecord.class))).thenReturn(drugApplicationDto);

        DrugApplicationDto result = drugService.createDrugApplication("12345");

        assertNotNull(result);
        assertEquals("12345", result.getApplicationNumber());
        verify(fdaClient, times(1)).getDrugApplicationByNumber("12345");
        verify(drugApplicationRepository, times(1)).save(drugApplicationRecord);
    }

    @Test
    void createDrugApplication_notFound() {
        when(fdaClient.getDrugApplicationByNumber(anyString())).thenThrow(new NoSuchElementException("Drug application not found"));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                drugService.createDrugApplication("12345"));

        assertEquals("Drug application not found", exception.getMessage());
        verify(fdaClient, times(1)).getDrugApplicationByNumber("12345");
        verify(drugApplicationRepository, never()).save(any());
    }

    @Test
    void getAllDrugApplications_success() {
        when(drugApplicationRepository.findAll()).thenReturn(List.of(drugApplicationRecord));
        when(mapper.toDto(any(DrugApplicationRecord.class))).thenReturn(drugApplicationDto);

        List<DrugApplicationDto> result = drugService.getAllDrugApplications();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("12345", result.get(0).getApplicationNumber());
        verify(drugApplicationRepository, times(1)).findAll();
    }

    @Test
    void getAllDrugApplications_empty() {
        when(drugApplicationRepository.findAll()).thenReturn(Collections.emptyList());

        List<DrugApplicationDto> result = drugService.getAllDrugApplications();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(drugApplicationRepository, times(1)).findAll();
    }
}

package com.gawel.medicine.client;

import com.gawel.medicine.client.fda.FdaClient;
import com.gawel.medicine.client.fda.exception.FdaApiException;
import com.gawel.medicine.client.fda.mapper.FdaResponseMapper;
import com.gawel.medicine.client.fda.response.FdaApiResponse;
import com.gawel.medicine.dto.DrugApplicationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@SpringBootTest
class FdaClientTest {

    private FdaClient fdaClient;

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private FdaResponseMapper fdaResponseMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fdaClient = new FdaClient(fdaResponseMapper, restTemplate);
        ReflectionTestUtils.setField(fdaClient, "fdaApiBaseUrl", "http://mocked-url.com");
    }

    @Test
    void fetchDrugs_success() {
        Map<String, Object> mockResponse = Map.of("total", 1, "results", Collections.singletonList("Test Drug"));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(new org.springframework.http.ResponseEntity<>(mockResponse, org.springframework.http.HttpStatus.OK));

        Map<String, Object> result = fdaClient.fetchDrugs("Test Manufacturer", "Test Brand", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.get("total"));
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }

    @Test
    void fetchDrugs_noDataFound() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        FdaApiException exception = assertThrows(FdaApiException.class, () ->
                fdaClient.fetchDrugs("Test Manufacturer", "Test Brand", 0, 10));

        assertEquals("Error calling FDA API: 404 NOT_FOUND", exception.getMessage());
    }

    @Test
    void fetchDrugs_apiError() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        FdaApiException exception = assertThrows(FdaApiException.class, () ->
                fdaClient.fetchDrugs("Test Manufacturer", "Test Brand", 0, 10));

        assertTrue(exception.getMessage().contains("Error calling FDA API"));
    }

    @Test
    void fetchDrugs_unexpectedError() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("Unexpected Error"));

        FdaApiException exception = assertThrows(FdaApiException.class, () ->
                fdaClient.fetchDrugs("Test Manufacturer", "Test Brand", 0, 10));

        assertTrue(exception.getMessage().contains("Unexpected error during FDA API call"));
    }

    @Test
    void getDrugApplicationByNumber_success() {
        FdaApiResponse mockResponse = new FdaApiResponse();
        mockResponse.setResults(Collections.singletonList(new FdaApiResponse.Result()));
        DrugApplicationDto mockDto = new DrugApplicationDto();
        mockDto.setApplicationNumber("12345");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(new org.springframework.http.ResponseEntity<>(mockResponse, org.springframework.http.HttpStatus.OK));
        when(fdaResponseMapper.toDrugApplicationDto(any())).thenReturn(mockDto);

        DrugApplicationDto result = fdaClient.getDrugApplicationByNumber("12345");

        assertNotNull(result);
        assertEquals("12345", result.getApplicationNumber());
    }

    @Test
    void getDrugApplicationByNumber_notFound() {
        FdaApiResponse mockResponse = new FdaApiResponse();
        mockResponse.setResults(Collections.emptyList());

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(new org.springframework.http.ResponseEntity<>(mockResponse, org.springframework.http.HttpStatus.OK));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                fdaClient.getDrugApplicationByNumber("12345"));

        assertEquals("Drug application not found in FDA by application number: 12345", exception.getMessage());
    }

    @Test
    void getDrugApplicationByNumber_multipleResults() {
        FdaApiResponse mockResponse = new FdaApiResponse();
        mockResponse.setResults(Collections.nCopies(2, new FdaApiResponse.Result()));
        DrugApplicationDto mockDto = new DrugApplicationDto();
        mockDto.setApplicationNumber("12345");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(new org.springframework.http.ResponseEntity<>(mockResponse, org.springframework.http.HttpStatus.OK));
        when(fdaResponseMapper.toDrugApplicationDto(any())).thenReturn(mockDto);

        DrugApplicationDto result = fdaClient.getDrugApplicationByNumber("12345");

        assertNotNull(result);
        assertEquals("12345", result.getApplicationNumber());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }
}

package com.gawel.medicine.client.fda;

import com.gawel.medicine.client.fda.exception.FdaApiException;
import com.gawel.medicine.client.fda.mapper.FdaResponseMapper;
import com.gawel.medicine.client.fda.response.FdaApiResponse;
import com.gawel.medicine.dto.DrugApplicationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
@Slf4j
public class FdaClient {

    @Value("${fda.api.base-url}")
    private String fdaApiBaseUrl;
    private final FdaResponseMapper fdaResponseMapper;
    private final RestTemplate restTemplate;

    /**
     * Search for drugs based on manufacturer name and brand name. Provides raw response from FDA API.
     */
    public Map<String, Object> fetchDrugs(String manufacturerName, String brandName, int page, int limit) {
        String query = buildSearchQuery(manufacturerName, brandName);
        return performApiCall(query, page, limit, new ParameterizedTypeReference<>() {});
    }

    /**
     * Get specific drug application by application number and map it to DrugApplication object.
     */
    public DrugApplicationDto getDrugApplicationByNumber(String applicationNumber) {
        String query = "application_number:" + applicationNumber;
        FdaApiResponse response = performApiCall(query, 0, 1, new ParameterizedTypeReference<>() {});
        if(response.getResults().isEmpty()) {
            throw new NoSuchElementException("Drug application not found in FDA by application number: " + applicationNumber);
        }
        if (response.getResults().size() > 1) {
            log.warn("More than one ({}) result found for application number: {}", response.getResults().size(), applicationNumber);
        }
        return fdaResponseMapper.toDrugApplicationDto(response.getResults().getFirst());
    }

    /**
     * Generic method to perform API calls with a parameterized type reference.
     */
    private <T> T performApiCall(String query, int page, int limit, ParameterizedTypeReference<T> responseType) {
        String url = buildUrl(query, page, limit);
        log.info("Calling FDA API with URL: {}", url);
        try {
            return restTemplate.exchange(url, HttpMethod.GET, null, responseType).getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException("Data not found in FDA by provided query: " + query);
        } catch (HttpClientErrorException e) {
            throw new FdaApiException("Error calling FDA API: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new FdaApiException("Unexpected error during FDA API call", e);
        }
    }


    private String buildUrl(String query, int page, int limit) {
        return UriComponentsBuilder.fromUriString(fdaApiBaseUrl)
                .queryParam("search", query)
                .queryParam("skip", page * limit)
                .queryParam("limit", limit)
                .build()
                .toUriString();
    }

    private String buildSearchQuery(String manufacturerName, String brandName) {
        String query = "openfda.manufacturer_name:" + manufacturerName;
        if (brandName != null && !brandName.isEmpty()) {
            query += "+AND+openfda.brand_name:" + brandName;
        }
        return query;
    }
}

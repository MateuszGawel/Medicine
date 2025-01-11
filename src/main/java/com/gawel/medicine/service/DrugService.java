package com.gawel.medicine.service;

import com.gawel.medicine.client.fda.FdaClient;
import com.gawel.medicine.dto.DrugApplicationDto;
import com.gawel.medicine.mapper.DrugApplicationMapper;
import com.gawel.medicine.model.DrugApplicationRecord;
import com.gawel.medicine.repository.DrugApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DrugService {

    private final FdaClient fdaClient;
    private final DrugApplicationRepository drugApplicationRepository;
    private final DrugApplicationMapper mapper;

    /**
     * Fetch drugs from FDA API based on manufacturer and brand name.
     *
     * @param manufacturerName manufacturer name
     * @param brandName        brand name
     * @param page             page number
     * @param limit            number of items per page
     * @return map with total number of items and list of drugs
     */
    public Map<String, Object> fetchDrugs(String manufacturerName, String brandName, int page, int limit) {
        Map<String, Object> response = fdaClient.fetchDrugs(manufacturerName, brandName, page, limit);
        log.info("Found drugs for manufacturer: {} and brand: {}", manufacturerName, brandName);
        return response;
    }

    /**
     * Create a new drug application based on the provided application number.
     *
     * @param applicationNumber application number
     * @return created drug application
     */
    public DrugApplicationDto createDrugApplication(String applicationNumber) {
        DrugApplicationDto drugApplicationDto = fdaClient.getDrugApplicationByNumber(applicationNumber);
        DrugApplicationRecord drugApplicationRecord = mapper.toRecord(drugApplicationDto);
        DrugApplicationRecord savedDrugApplicationRecord = drugApplicationRepository.save(drugApplicationRecord);
        log.info("Drug application created: {}", savedDrugApplicationRecord);
        return mapper.toDto(savedDrugApplicationRecord);
    }

    /**
     * Get all drug applications stored in the system.
     *
     * @return list of drug applications
     */
    public List<DrugApplicationDto> getAllDrugApplications() {
        List<DrugApplicationRecord> records = drugApplicationRepository.findAll();
        log.info("Found {} drug applications", records.size());
        return records.stream()
                .map(mapper::toDto)
                .toList();
    }

}

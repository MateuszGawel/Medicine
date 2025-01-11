package com.gawel.medicine.controller;

import com.gawel.medicine.dto.DrugApplicationDto;
import com.gawel.medicine.service.DrugService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drugs")
@AllArgsConstructor
@Slf4j
public class DrugController {

    private final DrugService drugService;

    @PostMapping("/application/{applicationNumber}")
    @Operation(
            summary = "Create drug application",
            description = "Creates a new drug application based on the provided application number."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Drug application created successfully"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DrugApplicationDto> createDrugApplication(@Valid @PathVariable String applicationNumber) {
        log.info("Creating drug application with number: {}", applicationNumber);
        DrugApplicationDto savedApplication = drugService.createDrugApplication(applicationNumber);
        return new ResponseEntity<>(savedApplication, HttpStatus.CREATED);
    }

    @GetMapping("/applications")
    @Operation(
            summary = "Get all drug applications",
            description = "Fetch all drug application records stored in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Drug applications retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<List<DrugApplicationDto>> getAllDrugApplications() {
        log.info("Fetching all drug applications");
        List<DrugApplicationDto> applications = drugService.getAllDrugApplications();
        return ResponseEntity.ok(applications);
    }
}

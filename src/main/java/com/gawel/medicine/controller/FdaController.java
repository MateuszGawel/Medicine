package com.gawel.medicine.controller;

import com.gawel.medicine.service.DrugService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/fda")
@AllArgsConstructor
@Slf4j
public class FdaController {

    private final DrugService drugService;

    @Operation(
            summary = "Search for FDA drug records",
            description = "Allows searching for drug records submitted to FDA based on manufacturer name and optionally brand name. " +
                    "Response is provided in raw FDA format and this endpoint plays a proxy role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Drugs found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> searchDrugs(
            @Parameter(description = "FDA Manufacturer name", required = true)
            @RequestParam String manufacturerName,

            @Parameter(description = "FDA Brand name (optional)")
            @RequestParam(required = false) String brandName,

            @Parameter(description = "Page number (default: 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Results per page (default: 10)")
            @RequestParam(defaultValue = "10") int limit
    ) {
        log.info("Searching for drugs with manufacturer: {} and brand: {}", manufacturerName, brandName);
        Map<String, Object> response = drugService.fetchDrugs(manufacturerName, brandName, page, limit);
        return ResponseEntity.ok(response);
    }
}

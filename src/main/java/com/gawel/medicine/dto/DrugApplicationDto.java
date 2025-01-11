package com.gawel.medicine.dto;

import lombok.Data;

import java.util.Set;

@Data
public class DrugApplicationDto {
    private String applicationNumber;
    private Set<String> manufacturerNames;
    private Set<String> substanceNames;
    private Set<String> productNumbers;
}

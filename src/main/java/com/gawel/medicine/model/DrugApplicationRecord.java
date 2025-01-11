package com.gawel.medicine.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document(collection = "drugApplication")
public class DrugApplicationRecord {
    @Id
    private String applicationNumber;

    private Set<String> manufacturerNames;

    private Set<String> substanceNames;

    private Set<String> productNumbers;
}

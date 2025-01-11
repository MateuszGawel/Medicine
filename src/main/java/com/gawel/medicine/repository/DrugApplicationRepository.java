package com.gawel.medicine.repository;

import com.gawel.medicine.model.DrugApplicationRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DrugApplicationRepository extends MongoRepository<DrugApplicationRecord, String> {

}

package com.gawel.medicine.mapper;

import com.gawel.medicine.dto.DrugApplicationDto;
import com.gawel.medicine.model.DrugApplicationRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DrugApplicationMapper {
    DrugApplicationRecord toRecord(DrugApplicationDto dto);
    DrugApplicationDto toDto(DrugApplicationRecord record);
}

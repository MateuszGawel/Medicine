package com.gawel.medicine.client.fda.mapper;

import com.gawel.medicine.client.fda.response.FdaApiResponse;
import com.gawel.medicine.dto.DrugApplicationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface FdaResponseMapper {
    @Mapping(target = "applicationNumber", source = "applicationNumber")
    @Mapping(target = "manufacturerNames", source = "openfda.manufacturerName")
    @Mapping(target = "substanceNames", source = "openfda.substanceName")
    @Mapping(target = "productNumbers", expression = "java(mapProductNumbers(result))")
    DrugApplicationDto toDrugApplicationDto(FdaApiResponse.Result result);

    default Set<String> mapProductNumbers(FdaApiResponse.Result result) {
        return result.getProducts() != null
                ? result.getProducts().stream()
                .map(FdaApiResponse.Product::getProductNumber)
                .collect(Collectors.toSet())
                : null;
    }
}

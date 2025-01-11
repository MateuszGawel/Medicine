package com.gawel.medicine.client.fda.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FdaApiResponse {
    private List<Result> results;

    @Data
    public static class Result {
        @JsonProperty("application_number")
        private String applicationNumber;
        private OpenFda openfda;
        private List<Product> products;
    }

    @Data
    public static class OpenFda {
        @JsonProperty("manufacturer_name")
        private List<String> manufacturerName;
        @JsonProperty("substance_name")
        private List<String> substanceName;
    }


    @Data
    public static class Product {
        @JsonProperty("product_number")
        private String productNumber;
    }
}

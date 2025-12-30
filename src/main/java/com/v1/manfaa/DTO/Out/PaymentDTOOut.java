package com.v1.manfaa.DTO.Out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTOOut {
    private String id;
    private String status;
    private Double amount;
    private String currency;
    private String description;
    private String amount_format;
    private String callback_url;
    private Source source;

    @Data
    public static class Source {
        private String type;
        private String company;

        @JsonProperty("transaction_url")
        private String transactionUrl;  // This is what you need!
    }
}

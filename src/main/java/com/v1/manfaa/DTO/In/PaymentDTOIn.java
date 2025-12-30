package com.v1.manfaa.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTOIn {
    @NotBlank(message = "name must not be empty")
    private String name;
    @NotBlank(message = "number must not be empty")
    private String number;
    @NotBlank(message = "cvc must not be empty")
    private String cvc;
    @NotBlank(message = "month must not be empty")
    private String month;
    @NotBlank(message = "year must not be empty")
    private String year;
    @NotNull(message = "amount must not be empty")
    private Double amount;
    @NotBlank(message = "currency must not be empty")
    private String currency;
    @NotBlank(message = "description must not be empty")
    private String description;
}

package com.v1.manfaa.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDTOIn {

    @NotBlank(message = "period is required")
    @Pattern(
            regexp = "MONTH|YEAR",
            message = "period must be either MONTH or YEAR"
    )
    private String subscriptionPeriod;
}


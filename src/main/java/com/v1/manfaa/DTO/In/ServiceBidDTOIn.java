package com.v1.manfaa.DTO.In;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.v1.manfaa.ValidationGroups.ValidationGroup1;
import com.v1.manfaa.ValidationGroups.ValidationGroup2;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ServiceBidDTOIn {
    private Integer serviceBidId;
    @Size(max = 500, min = 50, message = "description should be between 50 and 500", groups = ValidationGroup1.class)
    @NotBlank(message = "description should not be empty", groups = ValidationGroup1.class)
    private String description;
    @Size(max = 500, message = "notes should be between 50 and 500", groups = ValidationGroup2.class)
    @NotBlank(message = "notes on rejection should not be empty", groups = ValidationGroup2.class)
    private String notes;
    @Size(max = 500, min = 50, message = "deliverables should be between 50 and 500", groups = ValidationGroup1.class)
    @NotBlank(message = "deliverables should not be empty", groups = ValidationGroup1.class)
    private String deliverables;
    @NotNull(message = "estimated hours should not be empty", groups = ValidationGroup1.class)
    @Positive(message = "hours should be a positive number", groups = ValidationGroup1.class)
    private Double estimatedHours;

    private LocalDateTime proposedStartDate;
    private LocalDateTime proposedEndDate;
    @NotBlank(message = "exchange type should not be empty", groups = ValidationGroup1.class)
    @Pattern(regexp = "TOKENS|BARTER", message = "exchange Type should be TOKENS , BARTER", groups = ValidationGroup1.class)
    private String exchangeType;
    @NotNull(message = "token amount should not be null", groups = ValidationGroup1.class)
    @PositiveOrZero(message = "token amount should be zero or positive", groups = ValidationGroup1.class)
    private Double tokenAmount;
}

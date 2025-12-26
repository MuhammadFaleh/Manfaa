package com.v1.manfaa.DTO.In;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ServiceRequestDTOIn {
    private Integer serviceRequestId;
    private Integer id;
    @Size(max = 500, min = 50, message = "description should be between 50 and 500")
    @NotBlank(message = "description should not be empty")
    private String description;
    @Size(max = 500, min = 50, message = "deliverables should be between 50 and 500")
    @NotBlank(message = "deliverables should not be empty")
    private String deliverables;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate proposedStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate proposedEndDate;
    @NotBlank(message = "exchange type should not be empty")
    @Pattern(regexp = "TOKENS|BARTER|EITHER", message = "exchange Type should be TOKENS , BARTER or EITHER ")
    private String exchangeType;
    @NotNull(message = "token amount should not be null")
    @PositiveOrZero(message = "token amount should be zero or positive")
    private Double tokenAmount;
}

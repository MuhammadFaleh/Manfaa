package com.v1.manfaa.DTO.In;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.v1.manfaa.ValidationGroups.ValidationGroup1;
import com.v1.manfaa.ValidationGroups.ValidationGroup2;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ServiceRequestDTOIn {
    private Integer serviceRequestId;
    @Size(max = 100, min = 10, message = "title should be between 10 and 100")
    @NotBlank(message = "title should not be empty")
    private String title;
    @Size(max = 500, min = 20, message = "description should be between 20 and 500")
    @NotBlank(message = "description should not be empty")
    private String description;
    @Size(max = 500, min = 20, message = "deliverables should be between 20 and 500")
    @NotBlank(message = "deliverables should not be empty")
    private String deliverables;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in yyyy-MM-dd format")
    private String proposedStartDate;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in yyyy-MM-dd format")
    private String proposedEndDate;
    @NotNull(message = "token amount should not be null", groups = ValidationGroup1.class)
    @PositiveOrZero(message = "token amount should be zero or positive", groups = ValidationGroup1.class)
    private Double tokenAmount;
    @NotNull(message = "category must not be null")
    private Integer category;
    @NotNull(message = "category requested must not be null", groups = ValidationGroup2.class)
    private Integer categoryRequested;
}

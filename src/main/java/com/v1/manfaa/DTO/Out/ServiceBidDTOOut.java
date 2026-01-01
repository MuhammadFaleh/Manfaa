package com.v1.manfaa.DTO.Out;

import com.v1.manfaa.ValidationGroups.ValidationGroup1;
import com.v1.manfaa.ValidationGroups.ValidationGroup2;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ServiceBidDTOOut {
    private Integer serviceBidId;
    private String description;
    private String notes;
    private String deliverables;
    private Double estimatedHours;
    private LocalDate proposedStartDate;
    private LocalDate proposedEndDate;
    private String exchangeType;
    private Double tokenAmount;
}

package com.v1.manfaa.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDTOOut {

    private Integer id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}


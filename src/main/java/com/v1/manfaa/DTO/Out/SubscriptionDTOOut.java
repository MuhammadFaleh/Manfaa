package com.v1.manfaa.DTO.Out;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SubscriptionDTOOut {

    @Column(columnDefinition = "timestamp not null")
    private LocalDate startDate;

    @Column(columnDefinition = "timestamp not null")
    private LocalDate endDate;

    @Column(columnDefinition = "boolean not null")
    private Boolean isActive;

}

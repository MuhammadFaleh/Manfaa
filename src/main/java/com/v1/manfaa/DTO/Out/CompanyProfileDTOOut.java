package com.v1.manfaa.DTO.Out;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CompanyProfileDTOOut {

    private String name;
    private String industry;
    private Integer teamSize;
    private String description;
    private LocalDateTime createdAt;
    private boolean isSubscriber;

}

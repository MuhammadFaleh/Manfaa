package com.v1.manfaa.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewDTOOut {

    private Integer id;

    private Double rating;

    private String description;

    private LocalDateTime createdAt;

    // Reviewer Information
    private Integer reviewerId;
    private String reviewerName;
    private String reviewerIndustry;

    // Reviewed Company Information
    private Integer reviewedCompanyId;
    private String reviewedCompanyName;
    private String reviewedCompanyIndustry;

    // Contract Information
    private Integer contractId;
    private String exchangeType;
}
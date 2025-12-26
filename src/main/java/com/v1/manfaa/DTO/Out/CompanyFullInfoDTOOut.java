package com.v1.manfaa.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class CompanyFullInfoDTOOut {

    private String name;
    private String industry;
    private Integer teamSize;
    private String description;
    private LocalDate createdAt;
    private Boolean isSubscriber;

    private List<SkillsDTOOut> skills;

    private Double averageRating;
    private Integer totalReviews;
}

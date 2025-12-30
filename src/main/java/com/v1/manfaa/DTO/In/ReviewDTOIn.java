package com.v1.manfaa.DTO.In;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class ReviewDTOIn {


    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Double rating;

    @Size(min = 10, message = "description must be at least 10 characters")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate created_at;

}

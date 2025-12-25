package com.v1.manfaa.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewDTOOut {
    private Integer id;

    private Integer rating;

    private String description;
}

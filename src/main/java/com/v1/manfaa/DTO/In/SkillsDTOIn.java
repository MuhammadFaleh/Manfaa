package com.v1.manfaa.DTO.In;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillsDTOIn {

    @NotEmpty(message = "name should not be empty")

    private String name;

    @Size(min = 10, message = "description must be at least 10 characters")

    private  String description;
}

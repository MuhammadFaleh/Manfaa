package com.v1.manfaa.DTO.In;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyProfileDTOIn {

    @NotBlank(message = "name cannot be blank")
    @Size(max = 20)
    private String name;

    @NotBlank(message = "industry cannot be blank")
    @Size(max = 20)
    private String industry;

    @NotNull(message = "teamSize cannot be null")
    @Min(value = 1, message = "teamSize must be at least 1")
    private Integer teamSize;

    @NotBlank(message = "description cannot be blank")
    @Size(max = 1000)
    private String description;
}


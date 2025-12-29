package com.v1.manfaa.DTO.In;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditAdminDTO {
    @NotNull(message = "user id must not be null")
    private Integer userId;
    @Positive(message = "must be a positive number")
    private Double amount;
}

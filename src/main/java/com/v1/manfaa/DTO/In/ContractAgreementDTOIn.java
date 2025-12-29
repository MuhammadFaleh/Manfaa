package com.v1.manfaa.DTO.In;

import com.v1.manfaa.ValidationGroups.ValidationGroup1;
import com.v1.manfaa.ValidationGroups.ValidationGroup2;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class ContractAgreementDTOIn {
    private Integer ContractAgreementId;
    @NotNull(message = "request id must not be empty", groups = ValidationGroup1.class)
    private Integer requestId;
    @NotNull(message = "bid id must not be empty", groups = ValidationGroup1.class)
    private Integer bidId;
    @NotBlank(message = "delivery items must not be empty", groups = ValidationGroup2.class)
    @Size(max = 500, message = "delivery length must be no longer than 500 characters",groups = ValidationGroup2.class)
    private String delivery;
}

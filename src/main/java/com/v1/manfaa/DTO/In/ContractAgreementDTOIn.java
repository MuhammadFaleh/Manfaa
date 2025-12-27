package com.v1.manfaa.DTO.In;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class ContractAgreementDTOIn {
    private Integer ContractAgreementId;
    private Integer requestId;
    private Integer bidId;
}

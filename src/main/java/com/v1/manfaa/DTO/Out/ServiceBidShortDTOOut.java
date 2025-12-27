package com.v1.manfaa.DTO.Out;

import com.v1.manfaa.Model.CompanyProfile;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ServiceBidShortDTOOut {
    private Integer serviceBidId;
    private String CompanyName;
    private String paymentMethod;
    private Double tokenAmount;
    private String status;
    private LocalDateTime createdAt;
}

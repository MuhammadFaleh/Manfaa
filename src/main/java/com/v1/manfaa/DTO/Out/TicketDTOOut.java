package com.v1.manfaa.DTO.Out;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TicketDTOOut {

    private Integer id;

    private Integer contractId;

    private String companyName;

    private String title;

    private String body;

    private String category;

    private String priority;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    private String status;

}

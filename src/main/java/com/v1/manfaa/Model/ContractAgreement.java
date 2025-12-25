package com.v1.manfaa.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ContractAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "start_date", columnDefinition = "date not null")
    private LocalDate StartDate;
    @Column(name = "end_date", columnDefinition = "date not null")
    private LocalDate EndDate;
    @Column(name = "is_extended", columnDefinition = "boolean not null")
    private Boolean isExtended;
    @Column(name = "exchange_type", columnDefinition = "varchar(20) not null check(exchange_type = 'TOKENS' or exchange_type='BARTER')")
    private String exchangeType;
    @Column(name = "token_amount", columnDefinition = "double" )
    private Double tokenAmount;
    @Column(columnDefinition = "varchar(20) not null check(status = 'ACTIVE' or status='COMPLETED' or status='CANCELLED' or status='DISPUTED')")
    private String status;
    @Column(name = "created_at", columnDefinition = "timestamp not null")
    private LocalDateTime createdAt;
    @Column(name = "closed_at", columnDefinition = "timestamp")
    private LocalDateTime closedAt;
}

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
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "text not null check(Length(description)<=500)")
    private String description;
    @Column(columnDefinition = "text not null check(length(deliverables)<=500)")
    private String deliverables;
    @Column(name = "proposed_start_date", columnDefinition = "date not null")
    private LocalDate proposedStartDate;
    @Column(name = "proposed_end_date", columnDefinition = "date not null")
    private LocalDate proposedEndDate;
    @Column(name = "exchange_type", columnDefinition = "varchar(20) not null check(exchange_type = 'TOKENS' or exchange_type='BARTER' or exchange_type='EITHER')")
    private String exchangeType;
    @Column(name = "token_amount", columnDefinition = "double not null" )
    private Double tokenAmount;
    @Column(columnDefinition = "varchar(20) not null check(status = 'OPEN' or status='CLOSED' or status='CANCELLED')")
    private String status;
    @Column(name = "created_at", columnDefinition = "timestamp not null")
    private LocalDateTime createdAt;
    @Column(name = "closed_at", columnDefinition = "timestamp")
    private LocalDateTime closedAt;
}

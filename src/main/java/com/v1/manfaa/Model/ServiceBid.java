package com.v1.manfaa.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ServiceBid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "text not null Check(Length(description)<=500))")
    private String description;
    @Column(columnDefinition = "text not null Check(Length(deliverables)<=500))")
    private String deliverables;
    @Column(name = "estimated_hours", columnDefinition = "double not null")
    private Double estimatedHours;
    @Column(name = "proposed_start_date", columnDefinition = "date not null")
    private LocalDate proposedStartDate;
    @Column(columnDefinition = "date not null")
    private LocalDate proposed_end_date;
    @Column(columnDefinition = "varchar(20) not null check(payment_method = 'TOKENS' or payment_method='BARTER' or payment_method='EITHER')")
    private String payment_method;
    @Column(columnDefinition = )
    private Double token_amount;

    @Column(columnDefinition = "varchar(20) not null check(status = 'PENDING' or status='ACCEPTED' or status='REJECTED')")
    private String status;

}

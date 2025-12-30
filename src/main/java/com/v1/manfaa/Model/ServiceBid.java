package com.v1.manfaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(columnDefinition = "text not null check(length(description)<=500)")
    private String description;
    @Column(columnDefinition = "text check(length(notes)<=500)")
    private String notes;
    @Column(columnDefinition = "text not null check(length(deliverables)<=500)")
    private String deliverables;
    @Column(name = "estimated_hours", columnDefinition = "double not null")
    private Double estimatedHours;
    @Column(name = "proposed_start_date", columnDefinition = "date not null")
    private LocalDate proposedStartDate;
    @Column(name = "proposed_end_date", columnDefinition = "date not null")
    private LocalDate proposedEndDate;
    @Column(name = "payment_method", columnDefinition = "varchar(20) not null check(payment_method = 'TOKENS' or payment_method='BARTER')")
    private String paymentMethod;
    @Column(name = "token_amount", columnDefinition = "double" )
    private Double tokenAmount;
    @Column(columnDefinition = "varchar(20) not null check(status = 'PENDING' or status='ACCEPTED' or status='REJECTED')")
    private String status;
    @Column(name = "created_at", columnDefinition = "timestamp not null")
    private LocalDateTime createdAt;

    // relationships
    @ManyToOne
    @JsonIgnore
    private ServiceRequest serviceRequest;
    @ManyToOne
    @JsonIgnore
    private CompanyProfile companyProfile;
    @OneToOne(mappedBy = "serviceBid")
    @JsonIgnore
    private ContractAgreement contractAgreement;


}

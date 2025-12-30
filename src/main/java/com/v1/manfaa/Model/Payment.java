package com.v1.manfaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String currency;

    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JsonIgnore
    private CompanyProfile companyProfile;
}
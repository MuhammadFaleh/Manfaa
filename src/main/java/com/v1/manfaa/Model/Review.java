package com.v1.manfaa.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Entity
@Setter
@Getter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(columnDefinition = "integer not null")
    private Integer rating;

    @Column(columnDefinition = "text not null")
    private String description;

    @Column(columnDefinition = "Date not null")
    private LocalDate created_at;

   // @ManyToOne
   // @JsonIgnore
    // private CompanyProfile companyProfile;

    // Many to one with contract_agreement
}

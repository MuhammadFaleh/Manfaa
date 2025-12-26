package com.v1.manfaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@Entity
@Setter
@Getter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(255) not null")
    private String name;

    @Column(columnDefinition = " text not null")

    private String description;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "category")
    private Set<ServiceRequest> serviceRequests;



}

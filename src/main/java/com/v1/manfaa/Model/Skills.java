package com.v1.manfaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Entity
@Setter
@Getter
@NoArgsConstructor
public class Skills {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

@Column(columnDefinition = "varchar (255) not null")
    private String name;

@Column(columnDefinition = "text not null")
    private  String description;

    @ManyToOne
    @JsonIgnore
    private CompanyProfile companyProfile;


}

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
public class CompanyProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(20) not null")
    private String name;

    @Column(columnDefinition = "varchar(20) not null")
    private String industry;

    @Column(name = "team_size", columnDefinition = "int not null")
    private Integer teamSize;

    @Column(columnDefinition = "TEXT not null")
    private String description;

    @Column(name = "created_at", columnDefinition = "timestamp not null")
    private LocalDate createdAt;

    @Column(name = "is_subscriber", columnDefinition = "boolean not null")
    private boolean isSubscriber;

//    @OneToOne
//    @MapsId
//    private User user;

}

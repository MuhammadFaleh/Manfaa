package com.v1.manfaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
@Entity
@Setter
@Getter
@NoArgsConstructor
public class User implements UserDetails {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;

    @Column(columnDefinition = "varchar(255) not null unique")
    private String username;

    @Column(columnDefinition = "varchar(255) not null")
    private String password;

    @Column(columnDefinition = "varchar(255) not null unique")
    private String email;

    @Column(columnDefinition = "varchar(255) not null")
    private String fullName;

    @Column(columnDefinition = "varchar(15) not null unique")
    private String phone_Number;

    @Column(columnDefinition = "varchar(10) not null unique")
    private String recordNumber;


    @Column(columnDefinition = "varchar(20) not null")
    private String role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private CompanyProfile companyProfile;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

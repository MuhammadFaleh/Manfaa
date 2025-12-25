package com.v1.manfaa.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTOOut {


    private Integer id;

    private String username;

    private String email;

    private String fullName;

    private String phone_Number;

    private String role;
}

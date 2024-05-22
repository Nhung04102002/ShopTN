package com.nhung.shoptn.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String name;
    private String phone;
    private String email;
    private String password;

}

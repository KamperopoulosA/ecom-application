package com.app.ecom_application.dtos;

import lombok.Data;

@Data
public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private AddressDTO address;
}

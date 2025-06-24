package com.habithustle.habithustle_backend.DTO;

import lombok.Data;

@Data
public class UserRegistrationReq
{
    private String email;
    private String name;
    private String username;
    private String password;
    private String profileURL;
    private String role;

}

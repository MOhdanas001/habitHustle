package com.habithustle.habithustle_backend.DTO;

import lombok.Data;

@Data
public class LoginReq {
    private String identifier;
    private String password;
}

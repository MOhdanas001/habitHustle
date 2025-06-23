package com.habithustle.habithustle_backend.DTO;

import lombok.Data;

@Data
public class ResetPasswordreq {
    private String email;
    private String password;
}

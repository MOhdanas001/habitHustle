package com.habithustle.habithustle_backend.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ResetPasswordreq {
    private String email;
    private String password;

    @Data
    public static class ProofSubmissionDTO {
        private String betId;
        private String userId;
        private String proofUrl; // could be image/file
        private LocalDate date;  // day of proof
    }
}

package com.habithustle.habithustle_backend.DTO;

import com.habithustle.habithustle_backend.model.bet.BetParticipationStatus;
import com.habithustle.habithustle_backend.model.bet.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Data
public class SearchRequest {
    private String val;

    @Data
    public static class BetRequestDTO {
            private String name;
            private String description;
            private Double amount;

            private List<String> participantIds;
            private String verifierId;

            private LocalDateTime startDate;
            private LocalDateTime endDate;

            private List<DayOfWeek> taskDays;
            private Integer allowedOffDays;
        }

    @Data
    @Builder
    public static class Participants
    {
        private String userId;
        private PaymentStatus paymentStatus;          // UNPAID, PAID
        private BetParticipationStatus betStatus;     // NOT_STARTED, ACTIVE, FAILED, COMPLETED
        private HashMap<String,Integer> proofs;                  // Image URLs / file references
        private Integer usedOffDays;
    }
}

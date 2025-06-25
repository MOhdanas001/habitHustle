package com.habithustle.habithustle_backend.services;

import com.habithustle.habithustle_backend.DTO.SearchRequest;
import com.habithustle.habithustle_backend.model.Hustle;
import com.habithustle.habithustle_backend.model.bet.*;
import com.habithustle.habithustle_backend.repository.HustleRepository;
import com.habithustle.habithustle_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HustleService {

    private HustleRepository hustleRepository;
    private UserRepository  userRepository;

    public Object createBet(SearchRequest.BetRequestDTO req){

        if(req.getStartDate().isBefore(LocalDateTime.now())){
            return Map.of("status",0,
                   "message","Start Date must be in Future"
                     );
        }

        List<SearchRequest.Participants> participants = req.getParticipantIds().stream().map(id ->
                SearchRequest.Participants.builder()
                        .userId(id)
                        .paymentStatus(PaymentStatus.UNPAID)
                        .betStatus(BetParticipationStatus.NOT_STARTED)
                        .proofs(new ArrayList<>())
                        .usedOffDays(0)
                        .build()
        ).toList();


        Hustle bet = Hustle.builder()
                .name(req.getName())
                .description(req.getDescription())
                .amount(req.getAmount())
                .participants(participants)
                .verifierId(req.getVerifierId())
                .taskDays(req.getTaskDays())
                .allowedOffDays(req.getAllowedOffDays())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .betStatus(BetStatus.NOT_STARTED)
                .generalProofs(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Hustle saved = hustleRepository.save(bet);

        //Notify users via email or in-app
        return Map.of("status", 1, "message", "Bet created successfully", "betId", saved.getId());

    }


    public Object markUserAsPaid(String betId, String userId) {
        Optional<Hustle> optional = hustleRepository.findById(betId);
        if (optional.isEmpty()) return Map.of("status", 0, "message", "Bet not found");

        Hustle bet = optional.get();
        boolean updated = false;

        for (SearchRequest.Participants p : bet.getParticipants()) {
            if (p.getUserId().equals(userId) && p.getPaymentStatus() == PaymentStatus.UNPAID) {
                p.setPaymentStatus(PaymentStatus.PAID);
                updated = true;
            }
        }

        if (!updated) return Map.of("status", 0, "message", "User already paid or not in bet");

        // Check if all paid
        boolean allPaid = bet.getParticipants().stream()
                .allMatch(p -> p.getPaymentStatus() == PaymentStatus.PAID);

        if (allPaid) {
            bet.setBetStatus(BetStatus.ACTIVE);
            bet.getParticipants().forEach(p -> p.setBetStatus(BetParticipationStatus.ACTIVE));
        }

        bet.setUpdatedAt(LocalDateTime.now());
        hustleRepository.save(bet);

        return Map.of("status", 1, "message", "Payment recorded");
    }

}

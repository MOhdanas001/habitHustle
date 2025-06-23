package com.habithustle.habithustle_backend.model.wallet;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.habithustle.habithustle_backend.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    private String id;

    @DBRef
    private User user;

    private BigDecimal balance;

    private BigDecimal frozenAmount;

    @DBRef
    private List<Transaction> transactions = new ArrayList<>();
}

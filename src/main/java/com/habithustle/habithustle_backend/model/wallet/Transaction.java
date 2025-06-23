package com.habithustle.habithustle_backend.model.wallet;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;

@Document(collection = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    private String id;

    @DBRef
    private Wallet wallet;

    private BigDecimal amount;

    private TransactionType type;

    private String description;

    private BigDecimal balanceAfter;
}

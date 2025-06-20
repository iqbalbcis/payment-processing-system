package com.main.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.main.audit.Auditable;
import com.main.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.main.constants.CommonConstant.UTC_TIME_ZONE;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "transaction_tx")
public class Transaction extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_user_account", length = 8)
    private Long fromUserAccount;
    @Column(name = "to_user_account", length = 8)
    private Long toUserAccount;
    private double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @JsonFormat(timezone = UTC_TIME_ZONE)
    private LocalDateTime timestamp;
}

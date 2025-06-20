package com.main.entity;

import com.main.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user_tx")
public class User extends Auditable {

    @Id
    @Column(name = "account_number", length = 8)
    private Long accountNumber;
    private String email;
    private String name;
    private double balance;
}

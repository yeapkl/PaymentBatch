package com.yeapkl.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentDetails {

    @Id
    @GeneratedValue
    private int id;

    @NonNull
    private String accountNumber;

    @NonNull
    private BigDecimal trxAmount;

    @NonNull
    private String description;

    @NonNull
    private LocalDateTime trxDateTime;

    @NonNull
    private int customerId;
}

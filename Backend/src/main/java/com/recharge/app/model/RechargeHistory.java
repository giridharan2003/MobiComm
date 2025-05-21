package com.recharge.app.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "recharge_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RechargeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recharge_id")
    private Long rechargeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "price", nullable = false)
    private Double price;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "payment_mode", nullable = false)
    private String paymentMode;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "recharge_date",nullable = false)
    private Timestamp rechargeDate;
    
    @Column(name = "expiry_date",nullable = true)
    private Timestamp expiryDate;
}
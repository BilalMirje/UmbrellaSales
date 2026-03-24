package com.umbrellaevent.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sell_bill")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellBill extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "invoice_no", nullable = false)
    private String invoiceNo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sell_id", nullable = false)
    private Sell sell;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "to_pay", nullable = false)
    private Double toPay;

    @Column(name = "debit_amount", nullable = false)
    private Double debitAmount;

    @Column(name = "credit_amount", nullable = false)
    private Double creditAmount;

    @Column(nullable = false)
    private Double paid;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    
    @Column(nullable = true)
    private Double taxTotal;

}



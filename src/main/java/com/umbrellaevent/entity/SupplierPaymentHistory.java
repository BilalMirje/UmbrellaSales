package com.umbrellaevent.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "supplier_payment_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPaymentHistory extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "supplier_id", nullable = false)
    private UUID supplierId;

    @Column(name = "paid_amount", nullable = false)
    private Double paidAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType type;
}

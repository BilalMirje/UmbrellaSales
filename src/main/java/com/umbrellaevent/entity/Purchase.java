package com.umbrellaevent.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchase")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Purchase extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "invoice_no", nullable = false, unique = true)
    private String invoiceNo;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private List<ProductStock> productStocks;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "purchase_amount", nullable = false)
    private Double purchaseAmount;

    @Column(name = "total_purchase_amount", nullable = false)
    private Double totalPurchaseAmount;

    @Column(nullable = true)
    private Double cgst = 0.0;

    @Column(nullable = true)
    private Double sgst = 0.0;

    @Column(nullable = true)
    private Double igst =0.0;

    @Column(name = "payment_mode", nullable = false)
    private String paymentMode;

    // @Column(nullable = false)
    // private Double discount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;
}

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
@Table(name = "sell")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Sell extends Audit {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "invoice_no", nullable = false, unique = true)
    private String invoiceNo;

    @Column(name = "product_name", nullable = false)
    private String productName;

    // @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // @JoinColumn(name = "purchase_id")
    // private List<ProductStock> productStocks;
    
    @Column(name = "sell_date", nullable = false)
    private LocalDate sellDate;

    @Column
    private String chequeNo;
    
    @Column(name = "sell_amount", nullable = false)
    private Double sellAmount;

    @Column(name = "total_sell_amount", nullable = false)
    private Double totalSellAmount;

    @Column(nullable = true)
    private Double cgst = 0.0;

    @Column(nullable = true)
    private Double sgst = 0.0;

    @Column(nullable = true)
    private Double igst = 0.0;


    // @Column(name = "payment_mode", nullable = false)
    // private String paymentMode;
 private Double grandTotal;
    private Double taxTotal;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "sell_id")
    private List<SellStock> sellStocks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;
}

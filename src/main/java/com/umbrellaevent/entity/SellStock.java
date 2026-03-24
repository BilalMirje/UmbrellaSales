package com.umbrellaevent.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sell_stock")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellStock extends Audit {

     @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
     private UUID id;
    
     @Column(nullable = false)
private String productName;


    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false, unique = false)
    private String barcode;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private String hsnNo;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double rate;

    @Column(nullable = false)
    private Double amount;
}

package com.umbrellaevent.entity;

import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "supplier")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Supplier extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contact;

    @Column(name = "gst_no", nullable = false, unique = true)
    private String gstNo;

    @Column(nullable = false)
    private String address;

    @Column(name = "supply_type", nullable = false)
    private String supplyType;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "supplier_credit_id", nullable = false)
    private SupplierCredit supplierCredit;
}

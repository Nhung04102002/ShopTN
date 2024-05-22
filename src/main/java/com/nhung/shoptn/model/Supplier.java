package com.nhung.shoptn.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplierID")
    private Long supplierID;
    @Column(name = "supplierName", columnDefinition = "nvarchar(100)", unique = true)
    private String supplierName;
    @Column(name = "phone")
    private String phone;
    private String email;
    @Column(columnDefinition = "nvarchar(100)")
    private String address;
    @Column(columnDefinition = "nvarchar(255)")
    private String description;

    @Column(name = "isEnable",nullable = false)
    private int isEnable = 1;

    @Column(name = "isGroup",nullable = false)
    private boolean isGroup = false;


    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "parentID")
    private Supplier parentID;

    public Supplier() {
    }

    public Supplier(Long supplierID, String supplierName, String phone, String email, String address, String description, int isEnable, boolean isGroup, Supplier parentID) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.description = description;
        this.isEnable = isEnable;
        this.isGroup = isGroup;
        this.parentID = parentID;
    }
}

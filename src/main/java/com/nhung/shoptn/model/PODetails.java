package com.nhung.shoptn.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "PODetails")
public class PODetails {
    @Id
    @ManyToOne
    @JoinColumn(name = "purchaseID", nullable = false)
    @JsonIgnore
    private PurchaseReceipt purchaseReceipt;

    @Id
    @ManyToOne
    @JoinColumn(name = "productID", nullable = false)
    private Product product;

    @Column(columnDefinition = "INT default(0)")
    private int quantity;

    @Column(columnDefinition = "INT default(0)")
    private int price;

    @Column(name = "discount")
    private int discount;

    @Column(name = "totalAmount")
    private Long totalAmount;
}

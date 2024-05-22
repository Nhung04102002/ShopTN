package com.nhung.shoptn.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productID")
    private Long productID;

    @Column(name = "barcode", nullable = false)
    private String barcode;

    @Column(name = "productName", nullable = false, columnDefinition = "nvarchar(100)")
    private String productName;

    @Column(columnDefinition = "INT default(0)")
    private int price;

    @Column(columnDefinition = "INT default(0)")
    private int cogs;

    @Column(columnDefinition = "INT default(0)")
    private int quantity;

    @Column(columnDefinition = "INT default(0)")
    private int minQuantity;

    @Column(columnDefinition = "nvarchar(20)")
    private String unit;

    @Column(columnDefinition = "nvarchar(MAX)")
    private String description;

    @ManyToOne
    @JoinColumn(name = "categoryID", nullable = false)
    private Category category;

    @Column(name = "brand", columnDefinition = "nvarchar(50)")
    private String brand;

    @Column(name = "image")
    private String image;

    @Column(name = "status", columnDefinition = "INT default(1)")
    private int status;

    @Column(columnDefinition = "nvarchar(MAX)")
    private String location;

    @Column(columnDefinition = "nvarchar(MAX)")
    private String properties;

    @Transient
    public String getImagePath() {
        if (image == null || productID == null) return null;

        return "/images/" + productID + "/" + image;
    }

    @PostPersist
    public void setBarcode() {
        if (barcode == null || barcode.isEmpty()) {
            String paddedID = String.format("%06d", productID); // Chuyển productID thành chuỗi với độ dài là 5 và điền số 0 ở đầu
            barcode = "SP" + paddedID;
        }
    }
}

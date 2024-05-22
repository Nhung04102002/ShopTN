package com.nhung.shoptn.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity

@Table(name = "PurchaseReceipt")
public class PurchaseReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "purchaseID", nullable = false)
    private String purchaseID;

    @ManyToOne
    @JoinColumn(name = "creator", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "supplierID", nullable = false)
    private Supplier supplier;

    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "status", nullable = false)
    private int status = 1; //0-Huỷ, 1-Hoàn thành, 2-Phiếu tạm

    @Column(name = "statusPay", nullable = false)
    private Integer statusPay = 1; //0-Chưa trả, 1-Đã thanh toán, 2-Thanh toán 1 phần

    @Column(name = "amountPaid")
    private Long amountPaid;

    @Column(name = "totalAmount")
    private Long totalAmount;

    @Column(name = "discount")
    private int discount;

    @Column(name = "paymentMethod", nullable = false)
    private int paymentMethod;

    @ManyToOne
    @JoinColumn(name = "accNumber")
    private Account account;

    @PostPersist
    public void setCode() {
        String paddedID = String.format("%06d", id);
        purchaseID = "PO" + paddedID;
    }

}

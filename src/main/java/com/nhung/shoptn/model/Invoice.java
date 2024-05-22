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

@Table(name = "Invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoiceID")
    private Long invoiceID;

    @Column(name = "code", nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "customerID", nullable = false)
    private Customer customer;

    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "status", nullable = false)
    private int status = 1;

    @Column(name = "discount")
    private int discount;

    @Column(name = "paymentMethod", nullable = false)
    private int paymentMethod;

    @ManyToOne
    @JoinColumn(name = "accNumber")
    private Account account;

    @PrePersist
    public void setCode() {
        if (code == null || code.isEmpty()) {
            String paddedID = String.format("%06d", invoiceID);
            code = "HD" + paddedID;
        }
    }

}

package com.nhung.shoptn.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity


@Table(name = "Customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customerID")
    private Long customerID;

    @Column(name = "customerName", columnDefinition = "nvarchar(50)", nullable = false)
    private String customerName;

    @Column(columnDefinition = "varchar(128)", nullable = false)
    private String phone;

    private String email;

    private Date dob;

    @Column(columnDefinition = "nvarchar(100)")
    private String address;

    @Column(columnDefinition = "nvarchar(255)")
    private String description;

    @Column(name = "isEnable",nullable = false)
    private int isEnable = 1;

    @Column(nullable = false)
    private int gender = 1;
}

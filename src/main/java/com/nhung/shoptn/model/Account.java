package com.nhung.shoptn.model;

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
@Table(name = "Account")
public class Account {
    @Id
    @Column(name = "accNumber")
    private String accNumber;

    @ManyToOne
    @JoinColumn(name = "bankID", nullable = false)
    private Bank bank;

    @Column(name = "accOwner", columnDefinition = "nvarchar(50)", nullable = false)
    private String accOwner;

    @Column(name = "description")
    private String description;
}

package com.nhung.shoptn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditCartItem {
    private Long invoiceID;
    private Long productId;
    private String name;
    private int price;
    private int quantity;
    private int inventory;
    private int discount;
}

package com.nhung.shoptn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditPO {
    private Long poID;
    private Long productId;
    private String barcode;
    private String name;
    private String unit;
    private int cogs;
    private int quantity;
    private int discount;
    private int itemNumber;
}

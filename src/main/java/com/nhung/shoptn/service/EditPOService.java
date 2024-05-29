package com.nhung.shoptn.service;

import com.nhung.shoptn.dto.EditPO;
import com.nhung.shoptn.dto.PurchaseOrder;

import java.util.Collection;

public interface EditPOService {
    void add1(EditPO purchaseOrder);

    void remove(Long id);

    EditPO update1(Long productId, int cogs, int quantity, int discount);

    void clear();

    Collection<EditPO> getAllItems1();

    int getCount();

    int getAmount();

}

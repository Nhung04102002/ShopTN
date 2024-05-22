package com.nhung.shoptn.service;

import com.nhung.shoptn.dto.PurchaseOrder;

import java.util.Collection;

public interface PurchaseOrderService {
    void add(PurchaseOrder purchaseOrder);

    void remove(Long id);

    PurchaseOrder update(Long productId, int cogs, int quantity, int discount);

    void clear();

    Collection<PurchaseOrder> getAllItems();

    int getCount();

    int getAmount();
}

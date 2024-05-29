package com.nhung.shoptn.service;

import com.nhung.shoptn.dto.CartItem;
import com.nhung.shoptn.dto.EditCartItem;

import java.util.Collection;

public interface EditInvoiceService {
    void add(EditCartItem item);

    void remove(Long id);

    EditCartItem update(Long productId, int quantity, int discount);

    void clear();

    Collection<EditCartItem> getAllItems();

    int getCount();

    int getAmount();
}

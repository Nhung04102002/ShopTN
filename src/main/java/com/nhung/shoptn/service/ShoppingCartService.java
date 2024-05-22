package com.nhung.shoptn.service;

import com.nhung.shoptn.dto.CartItem;

import java.util.Collection;

public interface ShoppingCartService {
    void add(CartItem item);

    void remove(Long id);

    CartItem update(Long productId, int quantity, int discount);

    void clear();

    Collection<CartItem> getAllItems();

    int getCount();

    int getAmount();
}

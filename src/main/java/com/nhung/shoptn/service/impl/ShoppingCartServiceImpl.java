package com.nhung.shoptn.service.impl;

import com.nhung.shoptn.dto.CartItem;
import com.nhung.shoptn.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SessionScope
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    Map<Long, CartItem> maps = new HashMap<>();
    @Override
    public void add(CartItem item) {
        CartItem cartItem = maps.get(item.getProductId());
        if (cartItem == null) {
            maps.put(item.getProductId(),item);
        } else {
            cartItem.setQuantity(cartItem.getQuantity()+1);
        }
    }

    @Override
    public void remove(Long id){
        maps.remove(id);
    }

    @Override
    public CartItem update(Long productId, int quantity, int discount){
        CartItem cartItem = maps.get(productId);
        cartItem.setQuantity(quantity);
        if (cartItem.getQuantity() > cartItem.getInventory()) {
            cartItem.setQuantity(cartItem.getInventory());
        }
        if (cartItem.getQuantity() < 0){
            cartItem.setQuantity(1);
        }
        cartItem.setDiscount(discount);
        if (cartItem.getDiscount() > cartItem.getQuantity()*cartItem.getPrice()){
            cartItem.setDiscount(cartItem.getQuantity()*cartItem.getPrice());
        }
        if (cartItem.getDiscount() < 0){
            cartItem.setDiscount(0);
        }
        return cartItem;
    }

    @Override
    public void clear(){
        maps.clear();
    }

    @Override
    public Collection<CartItem> getAllItems(){
        return maps.values();
    }

    @Override
    public int getCount(){
        return maps.values().size();
    }

    @Override
    public int getAmount(){
        return maps.values().stream()
                .mapToInt(item -> (item.getQuantity() * item.getPrice())-item.getDiscount())
                .sum();
    }
}

package com.nhung.shoptn.service.impl;

import com.nhung.shoptn.dto.EditCartItem;
import com.nhung.shoptn.service.EditInvoiceService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SessionScope
@Service
public class EditInvoiceServiceImpl implements EditInvoiceService {
    Map<Long, EditCartItem> maps = new HashMap<>();
    @Override
    public void add(EditCartItem item) {
        EditCartItem cartItem = maps.get(item.getProductId());
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
    public EditCartItem update(Long productId, int quantity, int discount){
        EditCartItem cartItem = maps.get(productId);
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
    public Collection<EditCartItem> getAllItems(){
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

package com.nhung.shoptn.service.impl;

import com.nhung.shoptn.dto.EditPO;
import com.nhung.shoptn.service.EditPOService;
import com.nhung.shoptn.service.PurchaseOrderService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class POServiceImpl implements EditPOService {
    Map<Long, EditPO> maps = new HashMap<>();
    @Override
    public void add1(EditPO editPO) {
        EditPO po = maps.get(editPO.getProductId());
        if (po == null) {
            maps.put(editPO.getProductId(),editPO);
            updateOrderNumbers();
        } else {
            po.setQuantity(po.getQuantity()+1);
        }
    }

    @Override
    public void remove(Long id) {
        maps.remove(id);
        updateOrderNumbers();
    }

    @Override
    public EditPO update1(Long productId, int cogs, int quantity, int discount) {
        EditPO po = maps.get(productId);
        po.setCogs(cogs);
        if (po.getCogs() < 0){
            po.setCogs(0);
        }
        po.setQuantity(quantity);
        if (po.getQuantity() < 0){
            po.setQuantity(1);
        }
        po.setDiscount(discount);
        if (po.getDiscount() > po.getQuantity()*po.getCogs()){
            po.setDiscount(po.getQuantity()*po.getCogs());
        }
        if (po.getDiscount() < 0){
            po.setDiscount(0);
        }
        return po;
    }

    @Override
    public void clear() {
        maps.clear();
    }

    @Override
    public Collection<EditPO> getAllItems1() {
        updateOrderNumbers();
        return maps.values();
    }

    @Override
    public int getCount() {
        return maps.values().size();
    }

    @Override
    public int getAmount() {
        return maps.values().stream()
                .mapToInt(item -> (item.getQuantity() * item.getCogs())-item.getDiscount())
                .sum();
    }

    private void updateOrderNumbers() {
        int count = 1;
        for (EditPO order : maps.values()) {
            order.setItemNumber(count++);
        }
    }
}

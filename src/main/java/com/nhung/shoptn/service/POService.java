package com.nhung.shoptn.service;

import com.nhung.shoptn.model.InvoiceDetails;
import com.nhung.shoptn.model.PODetails;
import com.nhung.shoptn.model.PurchaseReceipt;
import com.nhung.shoptn.repository.PORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class POService {
    @Autowired private PORepository poRepository;

    public Page<List<Object[]>> filterPO(Date dateStart, Date dateEnd, Long creator, Integer method, Integer status, String keyword, Integer pageNo){
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return poRepository.filterPO(dateStart,dateEnd, creator, method, status, keyword, pageable);
    }

    public void cancelPO(Long id){
        poRepository.cancelPO(id);
    }

    public List<PODetails> getPODetailByID(Long id){
        return poRepository.getPODetailByID(id);
    }

    public PurchaseReceipt findByPOID(Long id){
        return poRepository.findByPOID(id);
    }

    public void deletePODs(Long id){
        poRepository.deletePODs(id);
    }
}

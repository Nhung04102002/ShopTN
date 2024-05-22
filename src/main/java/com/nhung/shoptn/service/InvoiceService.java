package com.nhung.shoptn.service;

import com.nhung.shoptn.model.Invoice;
import com.nhung.shoptn.model.InvoiceDetails;
import com.nhung.shoptn.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class InvoiceService {
    @Autowired private InvoiceRepository invoiceRepository;

    public Page<List<Object[]>> filterInvoice(Date dateStart, Date dateEnd, Long creator, Integer method, Integer status, String keyword, Integer pageNo){
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return invoiceRepository.filterInvoice(dateStart, dateEnd, creator, method, status, keyword, pageable);
    }

    public List<Object[]> getInvoiceDetails(){
        return invoiceRepository.getInvoiceDetails();
    }

    public List<Object[]> getTotalIds(){
        return invoiceRepository.getTotalIds();
    }

    public Invoice findByInvoiceID(Long invoiceID){
        return invoiceRepository.findByInvoiceID(invoiceID);
    }

    public Long sumTotalAmount(Date dateStart, Date dateEnd, Long creator, Integer method, Integer status, String keyword){
        return invoiceRepository.sumTotalAmount(dateStart, dateEnd, creator, method, status, keyword);
    }

    public int sumDiscount(Date dateStart, Date dateEnd, Long creator, Integer method, Integer status, String keyword){
        return invoiceRepository.sumDiscount(dateStart, dateEnd, creator, method, status, keyword);
    }

    public void cancelInvoice(Long invoiceID){
        invoiceRepository.cancelInvoice(invoiceID);
    }

    public List<InvoiceDetails> getInvoiceDetailByID(Long invoiceID){
        return invoiceRepository.getInvoiceDetailByID(invoiceID);
    }
}

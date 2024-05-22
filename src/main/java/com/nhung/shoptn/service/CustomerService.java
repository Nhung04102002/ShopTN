package com.nhung.shoptn.service;

import com.nhung.shoptn.exception.DataNotFoundException;
import com.nhung.shoptn.model.Customer;
import com.nhung.shoptn.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class CustomerService {

    @Autowired private CustomerRepository customerRepository;


    public Page<List<Object[]>> filterCus(Date dobStart, Date dobEnd, Integer gender, Integer status, Integer amountStart, Integer amountEnd, String keyword, Integer pageNo){
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return customerRepository.filterCus(dobStart, dobEnd, gender, status, amountStart, amountEnd, keyword, pageable);
    }

    public List<Object[]> historyTrans(){
        return customerRepository.historyTrans();
    }

    public Customer findCus(Long id) throws DataNotFoundException {
        Customer customer = customerRepository.findByCustomerID(id);
        if (customer != null){
            return customer;
        }
        throw new DataNotFoundException("Không tìm thấy khách hàng nào có ID là "+ id);
    }

    public int totalAmount(Date dobStart, Date dobEnd, Integer gender, Integer status, Integer amountStart, Integer amountEnd, String keyword){
        return customerRepository.totalAmount(dobStart, dobEnd, gender, status, amountStart, amountEnd, keyword);
    }

    public void deactivated(Long customerID){
        customerRepository.deactivated(customerID);
    }

    public void activated(Long customerID){
        customerRepository.activated(customerID);
    }

    public void deleted(Long customerID){
        customerRepository.deleted(customerID);
    }

}

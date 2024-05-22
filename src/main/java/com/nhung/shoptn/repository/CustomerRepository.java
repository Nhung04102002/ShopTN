package com.nhung.shoptn.repository;

import com.nhung.shoptn.model.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c from Customer c where c.customerID = :customerID")
    Customer findByCustomerID(@Param("customerID") Long customerID);

    @Transactional
    @Modifying
    @Query("update Customer c set c.isEnable = 2 where c.customerID = :customerID")
    void deactivated(@Param("customerID") Long customerID);

    @Transactional
    @Modifying
    @Query("update Customer c set c.isEnable = 1 where c.customerID = :customerID")
    void activated(@Param("customerID") Long customerID);

    @Transactional
    @Modifying
    @Query("update Customer c set c.isEnable = 0 where c.customerID = :customerID")
    void deleted(@Param("customerID") Long customerID);

    @Query("select c.customerID, c.customerName, c.dob, c.gender, c.phone, isnull(sum(ids.totalAmount),0), c.isEnable " +
            "from Customer c left join Invoice i on c.customerID = i.customer.customerID " +
            "left join InvoiceDetails ids on i.invoiceID = ids.invoice.invoiceID " +
            "where (cast(c.dob AS DATE) >= ?1 and cast(c.dob AS DATE) <= ?2 or ?1 is null or ?2 is null) " +
            "and (c.gender = ?3 or ?3 is null) and (c.isEnable = ?4 or ?4 is null) " +
            "and ((cast(c.customerID as string) like %?7%) or (c.customerName like %?7%) or (c.email like %?7%) or (c.phone like %?7%) or (c.address like %?7%) or ?7 is null ) " +
            "group by c.customerID, c.customerName, c.gender, c.dob, c.phone, c.isEnable " +
            "having isnull(sum(ids.totalAmount),0) >= ?5 and isnull(sum(ids.totalAmount),0) <= ?6 " +
            "order by c.customerID desc ")
    Page<List<Object[]>> filterCus(Date dobStart, Date dobEnd, Integer gender, Integer status, Integer amountStart, Integer amountEnd, String keyword, Pageable pageable);

    @Query("select isnull(sum(tb.Total), 0) from (" +
            "select c.customerID as CustomerID, isnull(sum(ids.totalAmount), 0) as Total " +
            "from Customer c " +
            "left join Invoice i on c.customerID = i.customer.customerID " +
            "left join InvoiceDetails ids on i.invoiceID = ids.invoice.invoiceID " +
            "where (cast(c.dob as date) >= ?1 and cast(c.dob as date) <= ?2 or ?1 is null or ?2 is null) " +
            "and (c.gender = ?3 or ?3 is null) " +
            "and (c.isEnable = ?4 or ?4 is null) " +
            "and ((cast(c.customerID as string) like %?7%) or (c.customerName like %?7%) or (c.email like %?7%) or (c.phone like %?7%) or (c.address like %?7%) or ?7 is null) " +
            "group by c.customerID " +
            "having isnull(sum(ids.totalAmount), 0) >= ?5 and isnull(sum(ids.totalAmount), 0) <= ?6) as tb")
    int totalAmount(Date dobStart, Date dobEnd, Integer gender, Integer status, Integer amountStart, Integer amountEnd, String keyword);

    @Query("select i.invoiceID, i.time, i.status, i.paymentMethod, isnull(sum(ids.totalAmount),0), c.customerID, i.code " +
            "from Customer c left join Invoice i on c.customerID = i.customer.customerID " +
            "left join InvoiceDetails ids on i.invoiceID = ids.invoice.invoiceID " +
            "group by i.invoiceID, i.time, i.status, i.paymentMethod, c.customerID, i.code " +
            "order by i.time desc ")
    List<Object[]> historyTrans ();
}

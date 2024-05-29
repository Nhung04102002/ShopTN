package com.nhung.shoptn.repository;

import com.nhung.shoptn.model.Supplier;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    @Query("SELECT s from Supplier s where s.supplierID = ?1")
    Supplier findBySupplierID(Long supplierID);

    @Transactional
    @Modifying
    @Query("update Supplier s set s.isEnable = 2 where s.supplierID = :supplierID")
    void deactivated(@Param("supplierID") Long supplierID);

    @Transactional
    @Modifying
    @Query("update Supplier s set s.isEnable = 1 where s.supplierID = :supplierID")
    void activated(@Param("supplierID") Long supplierID);

    @Transactional
    @Modifying
    @Query("update Supplier s set s.isEnable = 0 where s.supplierID = :supplierID")
    void deleted(@Param("supplierID") Long supplierID);

    @Query("select s from Supplier s where s.isGroup = true and s.isEnable = 1")
    List<Supplier> listGroup();

    @Query("select po.id, po.time, po.status, po.statusPay, isnull(po.totalAmount,0), s.supplierID, po.purchaseID " +
            "from Supplier s left join PurchaseReceipt po on s.supplierID = po.supplier.supplierID " +
            "order by po.time desc ")
    List<Object[]> historyTrans ();

    @Query("select po.id, po.time, isnull(po.totalAmount,0), isnull(po.amountPaid,0), s.supplierID, po.purchaseID " +
            "from Supplier s left join PurchaseReceipt po on s.supplierID = po.supplier.supplierID " +
            "where po.statusPay in (0,2) and po.status = 1 " +
            "order by po.time desc ")
    List<Object[]> debtSupplier();

    @Query("select distinct s.supplierID, s.supplierName, s.phone, s.email, " +
            "isnull(sum(case when po.status = 1 then po.totalAmount else 0 end),0), s.isEnable  " +
            "from Supplier s left join PurchaseReceipt po on s.supplierID = po.supplier.supplierID " +
            "where (s.parentID.supplierID = ?1 or ?1 is null) and (s.isEnable in ?2) and (s.isGroup = false) " +
            "and ((cast(s.supplierID as string) like %?3%) or (s.supplierName like %?3%) or (s.email like %?3%) or (s.phone like %?3%) or (s.address like %?3%) or (?3 is null)) " +
            "group by s.supplierID, s.supplierName, s.phone, s.email, s.isEnable " +
            "order by s.supplierID desc ")
    Page<List<Object[]>> filterSupplier(Long supplierID, List<Integer> status, String keyword, Pageable pageable);

    @Query("select isnull(sum(case when po.status = 1 then po.totalAmount else 0 end),0) " +
            "from PurchaseReceipt po left join Supplier s on s.supplierID = po.supplier.supplierID " +
            "where (s.parentID.supplierID = ?1 or ?1 is null) and (s.isEnable in ?2) and (s.isGroup = false) " +
            "and ((cast(s.supplierID as string) like %?3%) or (s.supplierName like %?3%) or (s.email like %?3%) or (s.phone like %?3%) or (s.address like %?3%) or (?3 is null))")
    int totalAmount(Long supplierID, List<Integer> status, String keyword);

    @Query("select distinct s.supplierID, s.supplierName, s.phone, s.email, " +
            "isnull(sum(case when po.status = 1 then po.totalAmount else 0 end),0), s.isEnable " +
            "from Supplier s left join PurchaseReceipt po on s.supplierID = po.supplier.supplierID " +
            "where (s.parentID.supplierID = ?1 or ?1 is null) and (po.statusPay in (0,2) and po.statusPay != 1) " +
            "and (s.isEnable in ?2) and (s.isGroup = false) " +
            "and ((cast(s.supplierID as string) like %?3%) or (s.supplierName like %?3%) or (s.email like %?3%) or (s.phone like %?3%) or (s.address like %?3%) or (?3 is null)) " +
            "group by s.supplierID, s.supplierName, s.phone, s.email, s.isEnable " +
            "order by s.supplierID desc ")
    Page<List<Object[]>> filterSupplier1(Long supplierID, List<Integer> status, String keyword, Pageable pageable);

    @Query("select isnull(sum(case when po.status = 1 then po.totalAmount else 0 end),0) " +
            "from PurchaseReceipt po left join Supplier s on s.supplierID = po.supplier.supplierID " +
            "where (s.parentID.supplierID = ?1 or ?1 is null) and (po.statusPay in (0,2) and po.statusPay != 1) " +
            "and (s.isEnable in ?2) and (s.isGroup = false) " +
            "and ((cast(s.supplierID as string) like %?3%) or (s.supplierName like %?3%) or (s.email like %?3%) or (s.phone like %?3%) or (s.address like %?3%) or (?3 is null))")
    int totalAmount1(Long supplierID, List<Integer> status, String keyword);

    @Query("select distinct s.supplierID, s.supplierName, s.phone, s.email, " +
            "isnull(sum(case when po.status = 1 then po.totalAmount else 0 end),0), s.isEnable  " +
            "from Supplier s left join PurchaseReceipt po on s.supplierID = po.supplier.supplierID " +
            "where (s.parentID.supplierID = ?1 or ?1 is null) and (s.isEnable in ?2) and (s.isGroup = false) and " +
            "s.supplierID not in (select pr.supplier.supplierID from PurchaseReceipt pr where pr.statusPay in (0,2)) " +
            "and ((cast(s.supplierID as string) like %?3%) or (s.supplierName like %?3%) or (s.email like %?3%) or (s.phone like %?3%) or (s.address like %?3%) or (?3 is null)) " +
            "group by s.supplierID, s.supplierName, s.phone, s.email, s.isEnable " +
            "order by s.supplierID desc ")
    Page<List<Object[]>> filterSupplier2(Long supplierID, List<Integer> status, String keyword, Pageable pageable);

    @Query("select isnull(sum(case when po.status = 1 then po.totalAmount else 0 end),0) " +
            "from PurchaseReceipt po left join Supplier s on s.supplierID = po.supplier.supplierID " +
            "where (s.parentID.supplierID = ?1 or ?1 is null) and (s.isEnable in ?2) and (s.isGroup = false) and " +
            "s.supplierID not in (select pr.supplier.supplierID from PurchaseReceipt pr where pr.statusPay in (0,2)) " +
            "and ((cast(s.supplierID as string) like %?3%) or (s.supplierName like %?3%) or (s.email like %?3%) or (s.phone like %?3%) or (s.address like %?3%) or (?3 is null))")
    int totalAmount2(Long supplierID, List<Integer> status, String keyword);

    @Query("select s.supplierName from Supplier s ")
    List<String> getSupplierNameList();
}

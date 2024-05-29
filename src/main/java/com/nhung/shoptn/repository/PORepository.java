package com.nhung.shoptn.repository;

import com.nhung.shoptn.model.Invoice;
import com.nhung.shoptn.model.InvoiceDetails;
import com.nhung.shoptn.model.PODetails;
import com.nhung.shoptn.model.PurchaseReceipt;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface PORepository extends JpaRepository<PurchaseReceipt, Long> {


    @Query("select tb1.purchaseID, tb1.time, tb1.supplier.supplierName, isnull(sum(tb2.totalAmount),0) , tb1.discount, tb1.status, tb1.id " +
            "from PODetails tb2 left join PurchaseReceipt tb1 on tb1.id = tb2.purchaseReceipt.id " +
            "where (cast(tb1.time AS DATE) >= ?1 and cast(tb1.time AS DATE) <= ?2 or ?1 is null or ?2 is null) and (tb1.user.id = ?3 or ?3 is null) and " +
            "(tb1.paymentMethod = ?4 or ?4 is null) and (tb1.status = ?5 or ?5 is null) and " +
            "(cast(tb1.id as string) like %?6% or tb1.supplier.supplierName like %?6% or tb1.purchaseID like %?6% or ?6 is null) " +
            "group by tb1.purchaseID, tb1.time, tb1.supplier.supplierName, tb1.discount, tb1.status, tb1.id " +
            "order by tb1.time desc")
    Page<List<Object[]>> filterPO(Date dateStart, Date dateEnd, Long creator, Integer method, Integer status, String keyword, Pageable pageable);

    @Query("select isnull(sum(tb2.totalAmount),0) " +
            "from PODetails tb2 left join PurchaseReceipt tb1 on tb1.id = tb2.purchaseReceipt.id " +
            "where (cast(tb1.time AS DATE) >= ?1 and cast(tb1.time AS DATE) <= ?2 or ?1 is null or ?2 is null) and (tb1.user.id = ?3 or ?3 is null) and " +
            "(tb1.paymentMethod = ?4 or ?4 is null) and (tb1.status = ?5 or ?5 is null) and " +
            "(cast(tb1.id as string) like %?6% or tb1.supplier.supplierName like %?6% or tb1.purchaseID like %?6% or ?6 is null) ")
    Long sumTotalAmount(Date dateStart, Date dateEnd, Long creator, Integer method, Integer status, String keyword);

    @Query("select isnull(sum(tb1.discount),0) " +
            "from PurchaseReceipt tb1 " +
            "where (cast(tb1.time AS DATE) >= ?1 and cast(tb1.time AS DATE) <= ?2 or ?1 is null or ?2 is null) and (tb1.user.id = ?3 or ?3 is null) and " +
            "(tb1.paymentMethod = ?4 or ?4 is null) and (tb1.status = ?5 or ?5 is null) and " +
            "(cast(tb1.id as string) like %?6% or tb1.supplier.supplierName like %?6% or tb1.purchaseID like %?6% or ?6 is null) ")
    int sumDiscount(Date dateStart, Date dateEnd, Long creator, Integer method, Integer status, String keyword);

    @Query("select isnull(sum(tb2.totalAmount),0), count(DISTINCT tb1.id) " +
            "from PurchaseReceipt tb1 join PODetails tb2 on tb1.id = tb2.purchaseReceipt.id " +
            "where cast(tb1.time AS DATE) = cast(current_date AS DATE) and tb1.status = 1 ")
    List<Object[]> getAmountAndCountPO();


    @Query("select p from PurchaseReceipt p where p.id = ?1")
    PurchaseReceipt findByPOID(Long purchaseID);

    @Query("select p.barcode, p.productName, tb2.quantity, tb2.price, tb2.discount, (tb2.quantity * tb2.price) - tb2.discount, tb1.purchaseID, tb1.id " +
            "from PurchaseReceipt tb1 join PODetails tb2 on tb1.id = tb2.purchaseReceipt.id " +
            "left join Product p on tb2.product.productID = p.productID ")
    List<Object[]> getPODetails();

    @Query("select tb1.purchaseID, sum(tb2.quantity), tb1.totalAmount, tb1.discount, tb1.id, tb1.amountPaid " +
            "from PurchaseReceipt tb1 join PODetails tb2 on tb1.id = tb2.purchaseReceipt.id " +
            "group by tb1.purchaseID, tb1.discount, tb1.id, tb1.amountPaid, tb1.totalAmount")
    List<Object[]> getTotalPOs();

    @Query("select max(po.id) from PurchaseReceipt po")
    int getMaxId();

    @Transactional
    @Modifying
    @Query(value = "insert into podetails (discount, price, quantity, total_amount, purchaseid, productid) " +
            "values (:discount, :cogs, :quantity, :totalAmount, :purchaseId, :productId)", nativeQuery = true)
    void savePODetail(@Param("purchaseId") Long purchaseId, @Param("discount") int discount,
                           @Param("productId") Long productId, @Param("quantity") int quantity,
                           @Param("cogs") int cogs, @Param("totalAmount") Long totalAmount);

    @Query("select po from PODetails po left join PurchaseReceipt p on po.purchaseReceipt.id = p.id " +
            "where po.product.productID = ?1 and p.status = 1")
    List<PODetails> findPODByProductId(Long productID);

    @Transactional
    @Modifying
    @Query("update PurchaseReceipt po set po.status = 0, po.statusPay = 1 where po.id = :id")
    void cancelPO(@Param("id") Long id);

    @Query("select po from PODetails po where po.purchaseReceipt.id = ?1")
    List<PODetails> getPODetailByID(Long id);

    @Transactional
    @Modifying
    @Query("delete PODetails po where po.purchaseReceipt.id = :id")
    void deletePODs(@Param("id") Long id);
}

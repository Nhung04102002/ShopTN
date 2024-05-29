package com.nhung.shoptn.repository;

import com.nhung.shoptn.model.Product;
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
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p where p.status in (1,2) order by p.productID desc ")
    List<Product> searchProduct();

    @Query("select p from Product p where p.productID = :productID and p.status in (1,2)")
    Product findByProductID(@Param("productID") Long productID);

    @Transactional
    @Modifying
    @Query("update Product p set p.status = 2 where p.productID = :productID")
    void deactivated(@Param("productID") Long productID);

    @Transactional
    @Modifying
    @Query("update Product p set p.status = 1 where p.productID = :productID")
    void activated(@Param("productID") Long productID);

    @Transactional
    @Modifying
    @Query("update Product p set p.status = 0 where p.productID = :productID")
    void deleted(@Param("productID") Long productID);

    @Query("select isnull(sum(p.quantity),0) from Product p where p.status in (1,2)")
    int totalQuantity();

    @Query("select distinct p.unit from Product p")
    List<String> unitList();
    @Query("select distinct p.brand from Product p")
    List<String> brandList();

    @Query("select i.product.productID, p.productName, isnull(sum(i.quantity),0) " +
            "from InvoiceDetails i left join Product p on i.product.productID = p.productID " +
            "left join Invoice tb2 on i.invoice.invoiceID = tb2.invoiceID " +
            "where tb2.status = 1 " +
            "group by i.product.productID, p.productName " +
            "ORDER BY ISNULL(SUM(i.quantity),0) DESC")
    List<Object[]> HighestQuantitySold(Pageable pageable);

    @Query("select i.product.productID, p.productName, isnull(sum(i.totalAmount),0) " +
            "from InvoiceDetails i left join Product p on i.product.productID = p.productID " +
            "left join Invoice tb2 on i.invoice.invoiceID = tb2.invoiceID " +
            "where tb2.status = 1 " +
            "group by i.product.productID, p.productName " +
            "ORDER BY ISNULL(SUM(i.totalAmount),0) DESC")
    List<Object[]> HighestAmountSold(Pageable pageable);

    @Query("select count(p.productID) from Product p where p.status in (1,2)")
    int countProduct();

    @Query("select s from Supplier s where s.isEnable in (1,2) and s.isGroup = false " +
            "order by s.supplierID desc ")
    List<Supplier> getSupplierList();

    @Query(value = "select distinct value " +
            "from STRING_SPLIT ((select STRING_AGG(p.location,'|') from Product p),'|') " +
            "WHERE value != '' and value is NOT NULL", nativeQuery = true)
    List<String> locationList();

    @Query("select distinct p " +
            "from Product p left join PODetails po on p.productID = po.product.productID " +
            "left join PurchaseReceipt pr on po.purchaseReceipt.id = pr.id " +
            "where (p.category.categoryID = ?1 or ?1 is null) and (p.status = ?2) " +
            "and (pr.supplier.supplierID in ?3 or ?3 is null) and " +
            "(p.location = ?4 or p.location like CONCAT('%', ?4, '|%') or p.location LIKE CONCAT('%|', ?4) or ?4 is null) " +
            "and (p.quantity between ?5 and (CASE WHEN ?6 = -1 THEN p.minQuantity ELSE ?6 END)) " +
            "and (p.productName like %?7% or p.barcode like %?7% or ?7 is null) " +
            "order by p.productID desc ")
    Page<Product> filterProduct(Long categoryID, int status, List<Long> supplier, String location, int min, int max, String keyword, Pageable pageable);

    @Query("select distinct p " +
            "from Product p left join PODetails po on p.productID = po.product.productID " +
            "left join PurchaseReceipt pr on po.purchaseReceipt.id = pr.id " +
            "where (p.category.categoryID = ?1 or ?1 is null) and " +
            "(pr.supplier.supplierID in ?2 or ?2 is null) and " +
            "(p.location = ?3 or p.location like CONCAT('%', ?3, '|%') or p.location LIKE CONCAT('%|', ?3) or ?3 is null) " +
            "and (p.quantity between ?4 and (CASE WHEN ?5 = -1 THEN p.minQuantity ELSE ?5 END)) and (p.status != 0) " +
            "and (p.productName like %?6% or p.barcode like %?6% or ?6 is null) " +
            "order by p.productID desc ")
    Page<Product> filterProduct1(Long categoryID, List<Long> supplier, String location, int min, int max, String keyword, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Product p set p.quantity = ?1 where p.productID = ?2")
    void updateQuantity(int quantity, Long productId);

    @Transactional
    @Modifying
    @Query("update Product p set p.cogs = ?1 where p.productID = ?2")
    void updateCogs(int cogs, Long productId);

    @Query("select distinct pod.product.productID, po.supplier.supplierID, po.supplier.supplierName from PODetails pod left join PurchaseReceipt po on pod.purchaseReceipt.id = po.id ")
    List<Object[]> getSupplierOfProduct();

}

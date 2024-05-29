package com.nhung.shoptn.repository;

import com.nhung.shoptn.model.Invoice;
import com.nhung.shoptn.model.InvoiceDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("select tb1.code, tb1.time, tb1.customer.customerName, isnull(sum(tb2.totalAmount),0) , tb1.discount, tb1.status, tb1.invoiceID " +
            "from InvoiceDetails tb2 left join Invoice tb1 on tb1.invoiceID = tb2.invoice.invoiceID " +
            "where (cast(tb1.time AS DATE) >= ?1 and cast(tb1.time AS DATE) <= ?2 or ?1 is null or ?2 is null) and (tb1.user.id = ?3 or ?3 is null) and " +
            "(tb1.paymentMethod = ?4 or ?4 is null) and (tb1.status = ?5 or ?5 is null) and " +
            "(cast(tb1.invoiceID as string) like %?6% or tb1.customer.customerName like %?6% or tb1.code like %?6% or ?6 is null) " +
            "group by tb1.code, tb1.time, tb1.customer.customerName, tb1.discount, tb1.status, tb1.invoiceID " +
            "order by tb1.time desc")
    Page<List<Object[]>> filterInvoice(Date dateStart, Date dateEnd, Long creator, Integer method, Integer status, String keyword, Pageable pageable);

    @Query("select isnull(sum(tb2.totalAmount),0) " +
            "from InvoiceDetails tb2 left join Invoice tb1 on tb1.invoiceID = tb2.invoice.invoiceID " +
            "where (cast(tb1.time AS DATE) >= ?1 and cast(tb1.time AS DATE) <= ?2 or ?1 is null or ?2 is null) and (tb1.user.id = ?3 or ?3 is null) and " +
            "(tb1.paymentMethod = ?4 or ?4 is null) and (tb1.status = ?5 or ?5 is null) and " +
            "(cast(tb1.invoiceID as string) like %?6% or tb1.customer.customerName like %?6% or tb1.code like %?6% or ?6 is null)")
    Long sumTotalAmount(Date dateStart, Date dateEnd, Long creator, Integer method, Integer status, String keyword);

    @Query("select isnull(sum(tb1.discount),0) " +
            "from Invoice tb1 " +
            "where (cast(tb1.time AS DATE) >= ?1 and cast(tb1.time AS DATE) <= ?2 or ?1 is null or ?2 is null) and (tb1.user.id = ?3 or ?3 is null) and " +
            "(tb1.paymentMethod = ?4 or ?4 is null) and (tb1.status = ?5 or ?5 is null) and " +
            "(cast(tb1.invoiceID as string) like %?6% or tb1.customer.customerName like %?6% or tb1.code like %?6% or ?6 is null)")
    int sumDiscount(Date dateStart, Date dateEnd, Long creator, Integer method, Integer status, String keyword);

    @Query("select isnull(sum(ids.totalAmount),0), count(DISTINCT i.invoiceID) " +
            "from InvoiceDetails ids join Invoice i on ids.invoice.invoiceID = i.invoiceID " +
            "where cast(i.time AS DATE) = cast(current_date AS DATE) and i.status = 1")
    List<Object[]> getAmountAndCountInvoice();

    @Query("select ids.product.productID, p.productName, isnull(sum(ids.totalAmount),0) " +
            "from InvoiceDetails ids join Invoice i on ids.invoice.invoiceID = i.invoiceID " +
            "left join Product p on ids.product.productID = p.productID " +
            "where cast(i.time AS DATE) = cast(current_date AS DATE) and i.status = 1" +
            "group by ids.product.productID, p.productName")
    List<Object[]> getAmountToday();
    // or CAST(i.time AS DATE) = CAST('2024-04-10' AS DATE)

    @Query("select i from Invoice i where i.invoiceID = ?1")
    Invoice findByInvoiceID(Long invoiceID);

    @Query("select p.barcode, p.productName, ids.quantity, ids.price, ids.discount, (ids.quantity * ids.price) - ids.discount, i.code, i.invoiceID " +
            "from InvoiceDetails ids left join Invoice i on ids.invoice.invoiceID = i.invoiceID " +
            "left join Product p on ids.product.productID = p.productID ")
    List<Object[]> getInvoiceDetails();

    @Query("select i.code, sum(ids.quantity), sum((ids.quantity * ids.price) - ids.discount), i.discount, i.invoiceID " +
            "from InvoiceDetails ids left join Invoice i on ids.invoice.invoiceID = i.invoiceID " +
            "group by i.code, i.discount, i.invoiceID")
    List<Object[]> getTotalIds();

    @Transactional
    @Modifying
    @Query(value = "insert into invoice_details (discount, price, quantity, total_amount, invoiceid, productid) " +
            "values (:discount, :price, :quantity, :totalAmount, :invoiceId, :productId)", nativeQuery = true)
    void saveInvoiceDetail(@Param("invoiceId") Long invoiceId, @Param("discount") int discount,
                           @Param("productId") Long productId, @Param("quantity") int quantity,
                           @Param("price") int price, @Param("totalAmount") Long totalAmount);

    @Query("select max(i.invoiceID) from Invoice i")
    int getMaxId();

    @Transactional
    @Modifying
    @Query("update Invoice i set i.status = 0 where i.invoiceID = :invoiceID")
    void cancelInvoice(@Param("invoiceID") Long invoiceID);

    @Query("select i from InvoiceDetails i where i.invoice.invoiceID = ?1")
    List<InvoiceDetails> getInvoiceDetailByID(Long invoiceID);

    @Transactional
    @Modifying
    @Query("delete InvoiceDetails i where i.invoice.invoiceID = :invoiceID")
    void deleteIDs(@Param("invoiceID") Long invoiceID);
}

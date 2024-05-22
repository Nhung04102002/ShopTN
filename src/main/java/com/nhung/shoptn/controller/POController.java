package com.nhung.shoptn.controller;

import com.nhung.shoptn.dto.PurchaseOrder;
import com.nhung.shoptn.exception.DatabaseAccessException;
import com.nhung.shoptn.model.*;
import com.nhung.shoptn.repository.*;
import com.nhung.shoptn.service.CategoryService;
import com.nhung.shoptn.service.POService;
import com.nhung.shoptn.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin")
@SessionAttributes("loggedInUser")
public class POController {
    @Autowired private UserRepository userRepository;
    @Autowired private PORepository poRepository;
    @Autowired private POService poService;
    @Autowired private PurchaseOrderService purchaseOrderService;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryService categoryService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private SupplierRepository supplierRepository;

    @ModelAttribute("loggedInUser")
    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return userRepository.findUserByEmail(user.getEmail());
    }

    @GetMapping("/PurchaseOrder")
    public String viewPOPage(Model model, @Param("keyword") String keyword,
                             @RequestParam(name = "timeFilter", defaultValue = "1") Integer timeFilter,
                             @Param("dateStart") java.sql.Date dateStart,
                             @Param("dateEnd") java.sql.Date dateEnd,
                             @Param("creatorFilter") Long creatorFilter,
                             @Param("methodFilter") Integer methodFilter,
                             @RequestParam(name = "statusFilter", defaultValue = "1") Integer statusFilter,
                             @RequestParam(name = "pageNo", defaultValue = "1") int pageNo) {

        java.sql.Date dStart, dEnd;
        if (dateStart != null || dateEnd != null ){
            timeFilter = 3;
        }

        if (timeFilter == 1){
            dStart = java.sql.Date.valueOf(LocalDate.now());
            dEnd = java.sql.Date.valueOf(LocalDate.now());
        } else if (timeFilter == 2){
            dStart = java.sql.Date.valueOf(LocalDate.now().minusDays(1));
            dEnd = java.sql.Date.valueOf(LocalDate.now().minusDays(1));
        } else {
            if(dateStart == null && dateEnd != null){
                dStart = java.sql.Date.valueOf("1930-01-01");
                dEnd = dateEnd;
            } else if(dateStart != null && dateEnd == null){
                dStart = dateStart;
                dEnd = java.sql.Date.valueOf(LocalDate.now());
            } else if (dateStart != null && dateEnd != null) {
                dStart = dateStart;
                dEnd = dateEnd;
            } else {
                dStart = null;
                dEnd = null;
            }
        }
        Long totalAmount;
        int totalDiscount;
        Page<List<Object[]>> poList;
        if (statusFilter == null || statusFilter == 3){
            poList = poService.filterPO(dStart, dEnd, creatorFilter,
                    methodFilter, null, keyword, pageNo);
            totalAmount = poRepository.sumTotalAmount(dStart, dEnd, creatorFilter,
                    methodFilter, null, keyword);
            totalDiscount = poRepository.sumDiscount(dStart, dEnd, creatorFilter,
                    methodFilter, null, keyword);
        } else {
            poList = poService.filterPO(dStart, dEnd, creatorFilter,
                    methodFilter, statusFilter, keyword, pageNo);
            totalAmount = poRepository.sumTotalAmount(dStart, dEnd, creatorFilter,
                    methodFilter, statusFilter, keyword);
            totalDiscount = poRepository.sumDiscount(dStart, dEnd, creatorFilter,
                    methodFilter, statusFilter, keyword);
        }
        model.addAttribute("sumTotalAmount", totalAmount);
        model.addAttribute("sumDiscount", totalDiscount);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalPage", poList.getTotalPages());
        model.addAttribute("dateStart", dateStart);
        model.addAttribute("dateEnd", dateEnd);
        model.addAttribute("timeFilter", timeFilter);
        model.addAttribute("methodFilter", methodFilter);
        model.addAttribute("creatorFilter", creatorFilter);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalItems", poList.getTotalElements());
        model.addAttribute("poList", poList);
        model.addAttribute("poDetails", poRepository.getPODetails());
        model.addAttribute("totalPOs", poRepository.getTotalPOs());
        model.addAttribute("userList", userRepository.findAll());
        return "/admin/purchase";
    }


    @GetMapping("/PurchaseOrder/findPO")
    @ResponseBody
    public PurchaseReceipt findPO(@RequestParam Long id) {
        return poRepository.findByPOID(id);
    }

    @GetMapping("/PurchaseOrder/new")
    public String viewNewPO(Model model) {
        model.addAttribute("searchP", productRepository.searchProduct());
        model.addAttribute("pList", productRepository.findAll());
        model.addAttribute("CART_ITEMS", purchaseOrderService.getAllItems());
        model.addAttribute("TOTAL", purchaseOrderService.getAmount());
        model.addAttribute("COUNT", purchaseOrderService.getCount());
        model.addAttribute("newPO", new PurchaseReceipt());
        String nextID = String.format("%06d", poRepository.getMaxId() + 1);
        model.addAttribute("nextPOId", "PO" + nextID);
        model.addAttribute("unitList", productRepository.unitList());
        model.addAttribute("brandList", productRepository.brandList());
        model.addAttribute("categoryList", categoryService.findAll());
        model.addAttribute("accList", accountRepository.findAll());
        model.addAttribute("sList", productRepository.getSupplierList());
        model.addAttribute("listGroup", supplierRepository.listGroup());
        return "/admin/newPO";
    }

    @GetMapping("/PurchaseOrder/cancel/{id}")
    public String cancelPO(@PathVariable Long id, RedirectAttributes ra){
        PurchaseReceipt purchaseReceipt = poService.findByPOID(id);
        try {
            poService.cancelPO(id);
            if (purchaseReceipt.getStatus() == 1){
                List<PODetails> poDetails = poService.getPODetailByID(id);
                for(PODetails item : poDetails) {
                    Product product = productRepository.findByProductID(item.getProduct().getProductID());
                    List<PODetails> details = poRepository.findPODByProductId(item.getProduct().getProductID());
                    int quantity = 0, total = 0;
                    for (PODetails pod : details){
                        quantity += pod.getQuantity();
                        total += pod.getQuantity() * pod.getPrice();
                    }
                    productRepository.updateCogs(total/quantity, product.getProductID());
                    productRepository.updateQuantity(product.getQuantity()-item.getQuantity(), product.getProductID());
                }
            }

            ra.addFlashAttribute("messageSuccess", "Huỷ phiếu nhập" + purchaseReceipt.getPurchaseID() + "thành công!");
        } catch (DatabaseAccessException e){
            ra.addFlashAttribute("messageFail", "Huỷ phiếu nhập " + purchaseReceipt.getPurchaseID() + " thất bại!");
        }
        return "redirect:/admin/PurchaseOrder";
    }

    @GetMapping("/PurchaseOrder/new/add/{id}")
    public String addItem(@PathVariable("id") Long id) {
        Product product = productRepository.findByProductID(id);
        if (product != null) {
            PurchaseOrder po = new PurchaseOrder();
            po.setProductId(product.getProductID());
            po.setBarcode(product.getBarcode());
            po.setName(product.getProductName());
            po.setUnit(product.getUnit());
            po.setCogs(0);
            po.setQuantity(1);
            po.setDiscount(0);
            purchaseOrderService.add(po);
        }
        return "redirect:/admin/PurchaseOrder/new";
    }

    @GetMapping("/PurchaseOrder/new/clear")
    public String clearAllItem() {
        purchaseOrderService.clear();
        return "redirect:/admin/PurchaseOrder/new";
    }

    @GetMapping("/PurchaseOrder/new/del/{id}")
    public String removeItem(@PathVariable("id") Long id) {
        purchaseOrderService.remove(id);
        return "redirect:/admin/PurchaseOrder/new";
    }

    @PostMapping("/PurchaseOrder/new/update")
    public String update(@RequestParam("id") Long id,
                         @RequestParam("cogs") String cogs,
                         @RequestParam("quantity") Integer quantity,
                         @RequestParam("discountItem") String discount) {
        int discount1, cogs1;
        if (cogs.contains(".")) {
            cogs1 = Integer.parseInt(cogs.replace(".", ""));
        } else {
            cogs1 = Integer.parseInt(cogs);
        }

        if (discount.contains(".")) {
            discount1 = Integer.parseInt(discount.replace(".", ""));
        } else {
            discount1 = Integer.parseInt(discount);
        }
        purchaseOrderService.update(id, cogs1, quantity, discount1);
        return "redirect:/admin/PurchaseOrder/new";
    }

    @PostMapping("/PurchaseOrder/new/saveP")
    public String saveP(Product product,
                        @RequestParam("price1") String price,
                        @RequestParam("fileImage") MultipartFile multipartFile,
                        RedirectAttributes ra) throws IOException {
        try {
            product.setStatus(1);
            product.setCogs(0);

            if (price != null && !price.isEmpty()) {
                product.setPrice(Integer.parseInt(price.replace(".", "")));
            } else {
                product.setPrice(0);
            }

            if (product.getProductID() != null) {
                Optional<Product> productOptional = productRepository.findById(product.getProductID());
                if (!multipartFile.isEmpty() && productOptional.isPresent()) {
                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                    product.setImage(fileName);
                    if (!fileName.equals(productOptional.get().getImage())) {
                        uploadImage(product, multipartFile, fileName);
                    }
                } else if (multipartFile.isEmpty() && productOptional.isPresent()) {
                    product.setImage(productOptional.get().getImage());
                }
                productRepository.save(product);
            } else {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                    product.setImage(fileName);
                    productRepository.save(product);
                    uploadImage(product, multipartFile, fileName);
                } else {
                    product.setImage(null);
                    productRepository.save(product);
                }
            }
            ra.addFlashAttribute("messageSuccess", "Cập nhật dữ liệu thành công!");
        } catch (DatabaseAccessException e) {
            ra.addFlashAttribute("messageFail", "Cập nhật dữ liệu thất bại!");
        }
        return "redirect:/admin/PurchaseOrder/new";
    }

    private void uploadImage(Product product, @RequestParam("fileImage") MultipartFile multipartFile, String fileName) throws IOException {
        ProductController.uploadImage1(product, multipartFile, fileName);
    }

    @PostMapping("/PurchaseOrder/new/import")
    public String importProduct(PurchaseReceipt po, @RequestParam(name = "action") String action,
                                @RequestParam(name = "discountAll") String discountAll,
                                @RequestParam(name = "amountPaidText") String amountPaid,
                                RedirectAttributes ra){
        try {
            po.setTime(new Date());
            po.setUser(loggedInUser());
            String nextID = String.format("%06d", poRepository.getMaxId()+1);
            po.setPurchaseID("PO" + nextID);
            if (discountAll.contains(".")){
                po.setDiscount(Integer.parseInt(discountAll.replace(".", "")));
            } else {
                po.setDiscount(Integer.parseInt(discountAll));
            }
            if (amountPaid.contains(".")){
                po.setAmountPaid(Long.parseLong(amountPaid.replace(".", "")));
            } else {
                po.setAmountPaid(Long.parseLong(amountPaid));
            }

            if (po.getStatusPay() == 0){
                po.setAmountPaid(0L);
            } else if (po.getStatusPay() == 1){
                po.setAmountPaid(po.getTotalAmount() - po.getDiscount());
            } else {
                po.setAmountPaid(po.getAmountPaid());
            }
            Collection<PurchaseOrder> cartItems = purchaseOrderService.getAllItems();

            if (action.equals("saveTemp")) {
                po.setStatus(2);
                poRepository.save(po);
                for(PurchaseOrder item : cartItems) {
                    poRepository.savePODetail(po.getId(), item.getDiscount(),
                            item.getProductId(), item.getQuantity(), item.getCogs(),
                            ((long) item.getQuantity() * item.getCogs() - item.getDiscount()));
                }
            } else {
                po.setStatus(1);
                poRepository.save(po);
                for(PurchaseOrder item : cartItems) {
                    Product product = productRepository.findByProductID(item.getProductId());

                    poRepository.savePODetail(po.getId(), item.getDiscount(),
                            item.getProductId(), item.getQuantity(), item.getCogs(),
                            ((long) item.getQuantity()*item.getCogs()-item.getDiscount()));
                    List<PODetails> poDetails = poRepository.findPODByProductId(item.getProductId());
                    int quantity = 0, total = 0;
                    for (PODetails pod : poDetails){
                        quantity += pod.getQuantity();
                        total += pod.getQuantity() * pod.getPrice();
                    }
                    productRepository.updateCogs(total/quantity, product.getProductID());
                    productRepository.updateQuantity(product.getQuantity()+item.getQuantity(), product.getProductID());
                }
            }
            purchaseOrderService.clear();
            ra.addFlashAttribute("messageSuccess", "Cập nhật hoá đơn thành công!");
        } catch (DatabaseAccessException e){
            ra.addFlashAttribute("messageFail", "Cập nhật hoá đơn thất bại!");
        }

        return "redirect:/admin/PurchaseOrder/new";
    }

    @PostMapping("/PurchaseOrder/new/supplier/save")
    public String addNewSupplier(Supplier supplier, RedirectAttributes ra){
        try {
            supplierRepository.save(supplier);
            ra.addFlashAttribute("messageSuccess", "Cập nhật dữ liệu thành công!");
        } catch (DataAccessException e){
            ra.addFlashAttribute("messageFail", "Cập nhật dữ liệu thất bại!");
        }
        return "redirect:/admin/PurchaseOrder/new";
    }

}



package com.nhung.shoptn.controller;

import com.nhung.shoptn.dto.EditPO;
import com.nhung.shoptn.exception.DatabaseAccessException;
import com.nhung.shoptn.model.*;
import com.nhung.shoptn.repository.*;
import com.nhung.shoptn.service.CategoryService;
import com.nhung.shoptn.service.POService;
import com.nhung.shoptn.service.impl.POServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/admin")
@SessionAttributes("loggedInUser")
public class EditPOController {
    @Autowired private UserRepository userRepository;
    @Autowired private PORepository poRepository;
    @Autowired private POService poService;
    @Autowired private POServiceImpl editPOService;
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

    @GetMapping("/PurchaseOrder/edit/{poID}")
    public String viewEditPO(Model model, @PathVariable Long poID) {
        Collection<EditPO> editPOS = editPOService.getAllItems1();
        if (!editPOS.isEmpty()){
            Optional<EditPO> editPO = editPOS.stream().findFirst();
            if (!Objects.equals(editPO.get().getPoID(), poID)){
                editPOService.clear();
            }
        }
        if (editPOS.isEmpty()){
            List<PODetails> poDetailsList = poService.getPODetailByID(poID);
            poDetailsList.forEach(poDetails -> {
                EditPO po = new EditPO();
                po.setPoID(poDetails.getPurchaseReceipt().getId());
                po.setProductId(poDetails.getProduct().getProductID());
                po.setBarcode(poDetails.getProduct().getBarcode());
                po.setName(poDetails.getProduct().getProductName());
                po.setUnit(poDetails.getProduct().getUnit());
                po.setCogs(poDetails.getPrice());
                po.setQuantity(poDetails.getQuantity());
                po.setDiscount(poDetails.getDiscount());
                editPOService.add1(po);
            });
        }
        PurchaseReceipt purchaseReceipt = poService.findByPOID(poID);

        model.addAttribute("searchP", productRepository.searchProduct());
        model.addAttribute("pList", productRepository.findAll());
        model.addAttribute("CART_ITEMS", editPOService.getAllItems1());
        model.addAttribute("TOTAL", editPOService.getAmount());
        model.addAttribute("COUNT", editPOService.getCount());
        model.addAttribute("editPO", purchaseReceipt);
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(purchaseReceipt.getTime());
        model.addAttribute("formattedDate", formattedDate);
        model.addAttribute("paymentMethod", purchaseReceipt.getPaymentMethod());
        model.addAttribute("statusPay", purchaseReceipt.getStatusPay());
        model.addAttribute("newPO", new PurchaseReceipt());
        model.addAttribute("unitList", productRepository.unitList());
        model.addAttribute("brandList", productRepository.brandList());
        model.addAttribute("categoryList", categoryService.findAll());
        model.addAttribute("accList", accountRepository.findAll());
        model.addAttribute("sList", productRepository.getSupplierList());
        model.addAttribute("listGroup", supplierRepository.listGroup());
        model.addAttribute("getSupplierNameList", supplierRepository.getSupplierNameList());
        return "/admin/editPO";
    }

    @GetMapping("/PurchaseOrder/edit/{poID}/add/{id}")
    public String addEditItem(@PathVariable Long id, @PathVariable Long poID) {
        Product product = productRepository.findByProductID(id);
        if (product != null) {
            EditPO po = new EditPO();
            po.setPoID(poID);
            po.setProductId(product.getProductID());
            po.setBarcode(product.getBarcode());
            po.setName(product.getProductName());
            po.setUnit(product.getUnit());
            po.setCogs(0);
            po.setQuantity(1);
            po.setDiscount(0);
            editPOService.add1(po);
        }
        return "redirect:/admin/PurchaseOrder/edit/" + poID;
    }

    @PostMapping("/PurchaseOrder/edit/{poID}/update")
    public String update1(@RequestParam("id") Long id,
                          @RequestParam("cogs") String cogs,
                          @RequestParam("quantity") Integer quantity,
                          @RequestParam("discountItem") String discount,
                          @PathVariable("poID") Long poID) {
        int discount1, cogs1;
        if (discount != null && !discount.trim().equals("")){
            if (discount.contains(".")) {
                discount1 = Integer.parseInt(discount.replace(".", ""));
            } else {
                discount1 = Integer.parseInt(discount);
            }
        } else {
            discount1 = 0;
        }

        if (cogs != null && !cogs.trim().equals("")){
            if (cogs.contains(".")) {
                cogs1 = Integer.parseInt(cogs.replace(".", ""));
            } else {
                cogs1 = Integer.parseInt(cogs);
            }
        } else {
            cogs1 = 0;
        }
        editPOService.update1(id, cogs1, quantity, discount1);
        return "redirect:/admin/PurchaseOrder/edit/" + poID;
    }

    @GetMapping("/PurchaseOrder/edit/{poID}/clear")
    public String clearAllItem1(@PathVariable("poID") Long poID) {
        editPOService.clear();
        return "redirect:/admin/PurchaseOrder/edit/" + poID;
    }

    @GetMapping("/PurchaseOrder/edit/{poID}/del/{id}")
    public String removeItem1(@PathVariable("id") Long id, @PathVariable("poID") Long poID) {
        editPOService.remove(id);
        return "redirect:/admin/PurchaseOrder/edit/" + poID;
    }

    @PostMapping("/PurchaseOrder/edit/{poID}/import")
    public String importProduct1(PurchaseReceipt po, @RequestParam(name = "action") String action,
                                 @PathVariable("poID") Long poID,
                                 @RequestParam(name = "discountAll") String discountAll,
                                 @RequestParam(name = "amountPaidText") String amountPaid,
                                 @RequestParam(name = "timeText") String timeText,
                                 RedirectAttributes ra){
        PurchaseReceipt purchaseReceipt = poService.findByPOID(poID);
        try {
            //set đầu phiếu theo dữ liệu cũ
            po.setId(poID);
            po.setPurchaseID(purchaseReceipt.getPurchaseID());
            po.setUser(purchaseReceipt.getUser());
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                po.setTime(formatter.parse(timeText));
            } catch (ParseException e){
                ra.addFlashAttribute("messageFail", "Định dạng ngày giờ không hợp lệ.");
            }

            if (discountAll != null && !discountAll.trim().equals("")){ //cập nhật discount đầu phiếu
                if (discountAll.contains(".")){
                    po.setDiscount(Integer.parseInt(discountAll.replace(".", "")));
                } else {
                    po.setDiscount(Integer.parseInt(discountAll));
                }
            } else {
                po.setDiscount(0);
            }

            if (amountPaid != null && !amountPaid.trim().equals("")){ //format và cập nhật số tiền đã trả
                if (amountPaid.contains(".")){
                    po.setAmountPaid(Long.parseLong(amountPaid.replace(".", "")));
                } else {
                    po.setAmountPaid(Long.parseLong(amountPaid));
                }
            } else {
                po.setAmountPaid(0L);
            }

            if (po.getStatusPay() == 0){ //cập nhật số tiền đã trả theo trạng thái thanh toán
                po.setAmountPaid(0L);
            } else if (po.getStatusPay() == 1){
                po.setAmountPaid(po.getTotalAmount() - po.getDiscount());
            } else {
                po.setAmountPaid(po.getAmountPaid());
            }

            Collection<EditPO> cartItems = editPOService.getAllItems1(); //Lấy ra tất cả item trong dto map EditPO

            //Cập nhật lại số lượng & giá vốn về ban đầu trước khi nhập nếu trạng thái là đã nhập hàng
            if (purchaseReceipt.getStatus() == 1){
                List<PODetails> poDetails = poService.getPODetailByID(poID);
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

            poService.deletePODs(poID); // Xoá chi tiết phiếu cũ
            // Lưu chi tiết phiếu mới
            if (action.equals("saveTemp")) {
                po.setStatus(2);
                poRepository.save(po);
                for(EditPO item : cartItems) {
                    poRepository.savePODetail(po.getId(), item.getDiscount(),
                            item.getProductId(), item.getQuantity(), item.getCogs(),
                            ((long) item.getQuantity() * item.getCogs() - item.getDiscount()));
                }
            } else {
                po.setStatus(1);
                poRepository.save(po);
                for(EditPO item : cartItems) {
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
            editPOService.clear();
            ra.addFlashAttribute("messageSuccess", "Cập nhật phiếu nhập " + purchaseReceipt.getPurchaseID() + " thành công!");
        } catch (DatabaseAccessException e){
            ra.addFlashAttribute("messageFail", "Cập nhật phiếu nhập " + purchaseReceipt.getPurchaseID() + " thất bại!");
        }

        return "redirect:/admin/PurchaseOrder/new";
    }

    @PostMapping("/PurchaseOrder/edit/{poID}/saveP")
    public String saveP1(Product product, @PathVariable("poID") Long poID,
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
            ra.addFlashAttribute("messageSuccess", "Thêm mới hàng hoá thành công!");
        } catch (DatabaseAccessException e) {
            ra.addFlashAttribute("messageFail", "Thêm mới hàng hoá thất bại!");
        }
        return "redirect:/admin/PurchaseOrder/edit/" + poID;
    }

    private void uploadImage(Product product, @RequestParam("fileImage") MultipartFile multipartFile, String fileName) throws IOException {
        ProductController.uploadImage1(product, multipartFile, fileName);
    }

    @PostMapping("/PurchaseOrder/edit/{poID}/supplier/save")
    public String addNewSupplier1(@PathVariable("poID") Long poID, Supplier supplier, RedirectAttributes ra){
        try {
            supplierRepository.save(supplier);
            ra.addFlashAttribute("messageSuccess", "Thêm mới nhà cung cấp thành công!");
        } catch (DataAccessException e){
            ra.addFlashAttribute("messageFail", "Thêm mới nhà cung cấp thất bại!");
        }
        return "redirect:/admin/PurchaseOrder/edit/" + poID;
    }



}



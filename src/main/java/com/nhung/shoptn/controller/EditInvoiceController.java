package com.nhung.shoptn.controller;

import com.nhung.shoptn.dto.EditCartItem;
import com.nhung.shoptn.dto.EditPO;
import com.nhung.shoptn.exception.DatabaseAccessException;
import com.nhung.shoptn.model.*;
import com.nhung.shoptn.repository.*;
import com.nhung.shoptn.service.EditInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/admin")
@SessionAttributes("loggedInUser")
public class EditInvoiceController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EditInvoiceService editInvoiceService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    InvoiceRepository invoiceRepository;


    @ModelAttribute("loggedInUser")
    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return userRepository.findUserByEmail(user.getEmail());
    }
    @GetMapping("/invoice/edit/{invoiceID}")
    public String viewCarts(Model model, @Param("keyword") String keyword,
                            @PathVariable("invoiceID") Long invoiceID){
        Collection<EditCartItem> editCartItems = editInvoiceService.getAllItems();
        if (!editCartItems.isEmpty()){
            Optional<EditCartItem> editInvoice = editCartItems.stream().findFirst();
            if (!Objects.equals(editInvoice.get().getInvoiceID(), invoiceID)){
                editInvoiceService.clear();
            }
        }
        if (editCartItems.isEmpty()){
            List<InvoiceDetails> invoiceDetails = invoiceRepository.getInvoiceDetailByID(invoiceID);
            invoiceDetails.forEach(item -> {
                Product product = productRepository.findByProductID(item.getProduct().getProductID());
                EditCartItem invoice = new EditCartItem();
                invoice.setInvoiceID(item.getInvoice().getInvoiceID());
                invoice.setProductId(item.getProduct().getProductID());
                invoice.setName(item.getProduct().getProductName());
                invoice.setInventory(item.getQuantity() + product.getQuantity());
                invoice.setQuantity(item.getQuantity());
                invoice.setDiscount(item.getDiscount());
                invoice.setPrice(item.getPrice());
                editInvoiceService.add(invoice);
            });
        }
        Invoice invoice = invoiceRepository.findByInvoiceID(invoiceID);

        model.addAttribute("keyword", keyword);
        model.addAttribute("pList", productRepository.findAll());
        Sort sort = Sort.by(Sort.Direction.DESC, "customerID");
        model.addAttribute("cList", customerRepository.findAll(sort));
        model.addAttribute("CART_ITEMS", editInvoiceService.getAllItems());
        model.addAttribute("TOTAL", editInvoiceService.getAmount());
        model.addAttribute("COUNT", editInvoiceService.getCount());
        model.addAttribute("accList", accountRepository.findAll());
        model.addAttribute("newInvoice", new Invoice());
        model.addAttribute("editInvoice", invoice);
        model.addAttribute("paymentMethod", invoice.getPaymentMethod());
        return "/admin/editInvoice";
    }

    @GetMapping("/invoice/edit/{invoiceID}/add/{id}")
    public String addCart(@PathVariable("id") Long id, @PathVariable("invoiceID") Long invoiceID){
        Product product = productRepository.findByProductID(id);
        if (product != null) {
            EditCartItem item = new EditCartItem();
            item.setInvoiceID(invoiceID);
            item.setProductId(product.getProductID());
            item.setName(product.getProductName());
            item.setPrice(product.getPrice());
            item.setQuantity(1);
            item.setDiscount(0);
            item.setInventory(product.getQuantity());
            editInvoiceService.add(item);
        }
        return "redirect:/admin/invoice/edit/" + invoiceID;
    }

    @GetMapping("/invoice/edit/{invoiceID}/clear")
    public String clearCart(@PathVariable("invoiceID") Long invoiceID){
        editInvoiceService.clear();
        return "redirect:/admin/invoice/edit/" + invoiceID;
    }

    @GetMapping("/invoice/edit/{invoiceID}/del/{id}")
    public String removeCart(@PathVariable("id") Long id, @PathVariable("invoiceID") Long invoiceID){
        editInvoiceService.remove(id);
        return "redirect:/admin/invoice/edit/" + invoiceID;
    }

    @PostMapping("/invoice/edit/{invoiceID}/update")
    public String update(@RequestParam("id") Long id,
                         @RequestParam("quantity") Integer quantity,
                         @RequestParam("discountItem") String discount,
                         @PathVariable("invoiceID") Long invoiceID){
        int discount1;
        if (discount != null && !discount.trim().equals("")){
            if (discount.contains(".")) {
                discount1 = Integer.parseInt(discount.replace(".", ""));
            } else {
                discount1 = Integer.parseInt(discount);
            }
        } else {
            discount1 = 0;
        }

        editInvoiceService.update(id, quantity, discount1);
        return "redirect:/admin/invoice/edit/" + invoiceID;
    }

    @PostMapping("/invoice/edit/{invoiceID}/checkout")
    public String checkout(Invoice invoice, @RequestParam(name = "discountAll") String discountAll,
                           @PathVariable("invoiceID") Long invoiceID, RedirectAttributes ra){
        Invoice invoice1 = invoiceRepository.findByInvoiceID(invoiceID);
        try {
            invoice.setInvoiceID(invoiceID);
            invoice.setStatus(1);
            invoice.setTime(invoice1.getTime());
            invoice.setUser(invoice1.getUser());
            invoice.setCode(invoice1.getCode());

            if (discountAll.contains(".")){
                invoice.setDiscount(Integer.parseInt(discountAll.replace(".", "")));
            } else {
                invoice.setDiscount(Integer.parseInt(discountAll));
            }

            List<InvoiceDetails> invoiceDetails = invoiceRepository.getInvoiceDetailByID(invoiceID);
            for(InvoiceDetails item : invoiceDetails) {
                Product product = productRepository.findByProductID(item.getProduct().getProductID());
                productRepository.updateQuantity(product.getQuantity()+item.getQuantity(), product.getProductID());
            }
            invoiceRepository.deleteIDs(invoiceID);

            invoiceRepository.save(invoice);

            Collection<EditCartItem> cartItems = editInvoiceService.getAllItems();
            for(EditCartItem item : cartItems) {
                Product product = productRepository.findByProductID(item.getProductId());
                invoiceRepository.saveInvoiceDetail(invoice.getInvoiceID(), item.getDiscount(),
                        item.getProductId(), item.getQuantity(), item.getPrice(),
                        ((long) item.getQuantity()*item.getPrice()-item.getDiscount()));
                productRepository.updateQuantity(product.getQuantity()-item.getQuantity(), product.getProductID());
            }
            editInvoiceService.clear();
            ra.addFlashAttribute("messageSuccess", "Cập nhật hoá đơn " + invoice1.getCode() + " thành công!");
        } catch (DatabaseAccessException e){
            ra.addFlashAttribute("messageFail", "Cập nhật hoá đơn " + invoice1.getCode() + " thất bại!");
        }
        return "redirect:/admin/invoice/sell";
    }

    @PostMapping("/invoice/edit/{invoiceID}/customer/save")
    public String addNewCustomer(Customer customer, @PathVariable("invoiceID") Long invoiceID, RedirectAttributes ra){
        try {
            customerRepository.save(customer);
            ra.addFlashAttribute("messageSuccess", "Thêm mới khách hàng thành công!");
        }catch (DatabaseAccessException e){
            ra.addFlashAttribute("messageFail", "Thêm mới khách hàng thất bại!");
        }
        return "redirect:/admin/invoice/edit/" + invoiceID;
    }
}

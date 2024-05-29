package com.nhung.shoptn.controller;

import com.nhung.shoptn.dto.CartItem;
import com.nhung.shoptn.exception.DataNotFoundException;
import com.nhung.shoptn.exception.DatabaseAccessException;
import com.nhung.shoptn.model.*;
import com.nhung.shoptn.repository.*;
import com.nhung.shoptn.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.Date;

@Controller
@RequestMapping("/admin")
@SessionAttributes("loggedInUser")
public class ShoppingCartController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ShoppingCartService cartService;
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
    @GetMapping("/invoice/sell")
    public String viewCarts(Model model, @Param("keyword") String keyword){
        model.addAttribute("keyword", keyword);
        model.addAttribute("pList", productRepository.findAll());
        Sort sort = Sort.by(Sort.Direction.DESC, "customerID");
        model.addAttribute("cList", customerRepository.findAll(sort));
        model.addAttribute("CART_ITEMS", cartService.getAllItems());
        model.addAttribute("TOTAL", cartService.getAmount());
        model.addAttribute("COUNT", cartService.getCount());
        model.addAttribute("accList", accountRepository.findAll());
        model.addAttribute("newInvoice", new Invoice());
        String nextID = String.format("%06d", invoiceRepository.getMaxId()+1);
        model.addAttribute("nextInvoiceId","HD" + nextID);
        return "/admin/newInvoice";
    }

    @GetMapping("/invoice/sell/add/{id}")
    public String addCart(@PathVariable("id") Long id){
        Product product = productRepository.findByProductID(id);
        if (product != null) {
            CartItem item = new CartItem();
            item.setProductId(product.getProductID());
            item.setName(product.getProductName());
            item.setPrice(product.getPrice());
            item.setQuantity(1);
            item.setDiscount(0);
            item.setInventory(product.getQuantity());
            cartService.add(item);
        }
        return "redirect:/admin/invoice/sell";
    }

    @GetMapping("/invoice/sell/clear")
    public String clearCart(){
        cartService.clear();
        return "redirect:/admin/invoice/sell";
    }

    @GetMapping("/invoice/sell/del/{id}")
    public String removeCart(@PathVariable("id") Long id){
        cartService.remove(id);
        return "redirect:/admin/invoice/sell";
    }

    @PostMapping("/invoice/sell/update")
    public String update(@RequestParam("id") Long id,
                         @RequestParam("quantity") Integer quantity,
                         @RequestParam("discountItem") String discount){
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

        cartService.update(id, quantity, discount1);
        return "redirect:/admin/invoice/sell";
    }

    @PostMapping("/invoice/sell/checkout")
    public String checkout(Invoice invoice, @RequestParam(name = "discountAll") String discountAll, RedirectAttributes ra){
        try {
            if (discountAll.contains(".")){
                invoice.setDiscount(Integer.parseInt(discountAll.replace(".", "")));
            } else {
                invoice.setDiscount(Integer.parseInt(discountAll));
            }
            invoice.setStatus(1);
            invoice.setTime(new Date());
            invoice.setUser(loggedInUser());
            String nextID = String.format("%06d", invoiceRepository.getMaxId()+1);
            invoice.setCode("HD" + nextID);
            invoiceRepository.save(invoice);
            Collection<CartItem> cartItems = cartService.getAllItems();
            for(CartItem item : cartItems) {
                Product product = productRepository.findByProductID(item.getProductId());
                invoiceRepository.saveInvoiceDetail(invoice.getInvoiceID(), item.getDiscount(),
                        item.getProductId(), item.getQuantity(), item.getPrice(),
                        ((long) item.getQuantity()*item.getPrice()-item.getDiscount()));
                productRepository.updateQuantity(product.getQuantity()-item.getQuantity(), product.getProductID());
            }
            cartService.clear();
            ra.addFlashAttribute("messageSuccess", "Thêm mới hoá đơn thành công!");
        } catch (DatabaseAccessException e){
            ra.addFlashAttribute("messageFail", "Thêm mới hoá đơn thất bại!");
        }
        return "redirect:/admin/invoice/sell";
    }

    @PostMapping("/invoice/sell/customer/save")
    public String addNewCustomer(Customer customer, RedirectAttributes ra){
        try {
            customerRepository.save(customer);
            ra.addFlashAttribute("messageSuccess", "Thêm mới khách hàng thành công!");
        }catch (DatabaseAccessException e){
            ra.addFlashAttribute("messageFail", "Thêm mới khách hàng thất bại!");
        }
        return "redirect:/admin/invoice/sell";
    }
}

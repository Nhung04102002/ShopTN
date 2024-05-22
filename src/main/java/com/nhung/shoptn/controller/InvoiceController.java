package com.nhung.shoptn.controller;

import com.nhung.shoptn.dto.CartItem;
import com.nhung.shoptn.exception.DatabaseAccessException;
import com.nhung.shoptn.model.Invoice;
import com.nhung.shoptn.model.InvoiceDetails;
import com.nhung.shoptn.model.Product;
import com.nhung.shoptn.model.User;
import com.nhung.shoptn.repository.InvoiceRepository;
import com.nhung.shoptn.repository.ProductRepository;
import com.nhung.shoptn.repository.UserRepository;
import com.nhung.shoptn.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
@SessionAttributes("loggedInUser")
public class InvoiceController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ProductRepository productRepository;


    @ModelAttribute("loggedInUser")
    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return userRepository.findUserByEmail(user.getEmail());
    }

    @GetMapping("/invoice")
    public String viewInvoicePage(Model model, @Param("keyword") String keyword,
                                  @RequestParam(name = "timeFilter", defaultValue = "1") Integer timeFilter,
                                  @Param("dateStart") Date dateStart,
                                  @Param("dateEnd") Date dateEnd,
                                  @Param("creatorFilter") Long creatorFilter,
                                  @Param("methodFilter") Integer methodFilter,
                                  @RequestParam(name = "statusFilter", defaultValue = "1") Integer statusFilter,
                                  @RequestParam(name = "pageNo", defaultValue = "1") int pageNo){
        Date dStart, dEnd;
        if (dateStart != null || dateEnd != null ){
            timeFilter = 3;
        }

        if (timeFilter == 1){
            dStart = Date.valueOf(LocalDate.now());
            dEnd = Date.valueOf(LocalDate.now());
        }   else if (timeFilter == 2){
            dStart = Date.valueOf(LocalDate.now().minusDays(1));
            dEnd = Date.valueOf(LocalDate.now().minusDays(1));
        } else {
            if(dateStart == null && dateEnd != null){
                dStart = Date.valueOf("1930-01-01");
                dEnd = dateEnd;
            } else if(dateStart != null && dateEnd == null){
                dStart = dateStart;
                dEnd = Date.valueOf(LocalDate.now());
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
        Page<List<Object[]>> invoices;
        if (statusFilter == null || statusFilter == 3){
            invoices = invoiceService.filterInvoice(dStart, dEnd, creatorFilter,
                    methodFilter, null, keyword, pageNo);
            totalAmount = invoiceService.sumTotalAmount(dStart, dEnd, creatorFilter,
                    methodFilter, null, keyword);
            totalDiscount = invoiceService.sumDiscount(dStart, dEnd, creatorFilter,
                    methodFilter, null, keyword);
        } else {
            invoices = invoiceService.filterInvoice(dStart, dEnd, creatorFilter,
                    methodFilter, statusFilter, keyword, pageNo);
            totalAmount = invoiceService.sumTotalAmount(dStart, dEnd, creatorFilter,
                    methodFilter, statusFilter, keyword);
            totalDiscount = invoiceService.sumDiscount(dStart, dEnd, creatorFilter,
                    methodFilter, statusFilter, keyword);
        }
        model.addAttribute("sumTotalAmount", totalAmount);
        model.addAttribute("sumDiscount", totalDiscount);
        model.addAttribute("keyword", keyword);
        model.addAttribute("dateStart", dateStart);
        model.addAttribute("dateEnd", dateEnd);
        model.addAttribute("timeFilter", timeFilter);
        model.addAttribute("methodFilter", methodFilter);
        model.addAttribute("creatorFilter", creatorFilter);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("totalPage", invoices.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalItems", invoices.getTotalElements());
        model.addAttribute("invoices", invoices);
        model.addAttribute("invoiceDetails", invoiceService.getInvoiceDetails());
        model.addAttribute("totalIds", invoiceService.getTotalIds());
        model.addAttribute("userList", userRepository.findAll());
        return "/admin/invoice";
    }


    @GetMapping("/invoice/findInvoice")
    @ResponseBody
    public Invoice findInvoice(@RequestParam Long invoiceID){
        return invoiceService.findByInvoiceID(invoiceID);
    }

    @GetMapping("/invoice/cancel/{invoiceID}")
    public String cancelInvoice(@PathVariable Long invoiceID, RedirectAttributes ra){
        try {
            invoiceService.cancelInvoice(invoiceID);
            List<InvoiceDetails> invoiceDetails = invoiceService.getInvoiceDetailByID(invoiceID);
            for(InvoiceDetails item : invoiceDetails) {
                Product product = productRepository.findByProductID(item.getProduct().getProductID());
                productRepository.updateQuantity(product.getQuantity()+item.getQuantity(), product.getProductID());
            }
            ra.addFlashAttribute("messageSuccess", "Huỷ hoá đơn thành công!");
        } catch (DatabaseAccessException e){
            ra.addFlashAttribute("messageFail", "Huỷ hoá đơn thất bại!");
        }
        return "redirect:/admin/invoice";
    }
}

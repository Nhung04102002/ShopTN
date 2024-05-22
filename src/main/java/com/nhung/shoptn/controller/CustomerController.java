package com.nhung.shoptn.controller;

import com.nhung.shoptn.exception.DataNotFoundException;
import com.nhung.shoptn.exception.DatabaseAccessException;
import com.nhung.shoptn.model.Customer;
import com.nhung.shoptn.model.User;
import com.nhung.shoptn.repository.CustomerRepository;
import com.nhung.shoptn.repository.UserRepository;
import com.nhung.shoptn.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
@SessionAttributes("loggedInUser")
public class CustomerController {
    @Autowired private UserRepository userRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private CustomerService customerService;

    @ModelAttribute("loggedInUser")
    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return userRepository.findUserByEmail(user.getEmail());
    }

    @GetMapping("/customer")
    public String viewCustomerPage(Model model, @Param("keyword") String keyword,
                                   @RequestParam(name = "dobFilter", defaultValue = "1") Integer dobFilter,
                                   @Param("dobStart") Date dobStart,
                                   @Param("dobEnd") Date dobEnd,
                                   @Param("genderFilter") Integer genderFilter,
                                   @RequestParam(name="statusFilter", defaultValue = "1") Integer statusFilter,
                                   @Param("amountStart") Integer amountStart,
                                   @Param("amountEnd") Integer amountEnd,
                                   @RequestParam(name = "pageNo", defaultValue = "1") int pageNo){
        Date dStart, dEnd;
        if (dobStart != null || dobEnd != null ){
            dobFilter = 2;
        }

        if (dobFilter == 1){
            dStart = Date.valueOf("1930-01-01");
            dEnd = Date.valueOf(LocalDate.now());
        } else {
            if (dobStart == null && dobEnd != null){
                dStart = Date.valueOf("1930-01-01");
                dEnd = dobEnd;
            } else if (dobStart != null && dobEnd == null){
                dStart = dobStart;
                dEnd = Date.valueOf(LocalDate.now());
            } else if (dobStart != null && dobEnd != null) {
                dStart = dobStart;
                dEnd = dobEnd;
            } else {
                dStart = null;
                dEnd = null;
            }
        }
        int aStart, aEnd, totalAmount;
        if(amountStart == null && amountEnd == null){
            aStart = 0;
            aEnd = 999999999;
        } else if(amountStart != null && amountEnd == null){
            aStart = amountStart;
            aEnd = 999999999;
        } else if (amountStart == null && amountEnd != null){
            aStart = 0;
            aEnd = amountEnd;
        } else {
            aStart = amountStart;
            aEnd = amountEnd;
        }

        Page<List<Object[]>> customerList;
        if (statusFilter == null || statusFilter == 3){
            customerList = customerService.filterCus(dStart, dEnd, genderFilter, null, aStart, aEnd, keyword, pageNo);
            totalAmount = customerService.totalAmount(dStart, dEnd, genderFilter, null, aStart, aEnd, keyword);
        } else {
            customerList = customerService.filterCus(dStart, dEnd, genderFilter, statusFilter, aStart, aEnd, keyword, pageNo);
            totalAmount = customerService.totalAmount(dStart, dEnd, genderFilter, statusFilter, aStart, aEnd, keyword);
        }
        model.addAttribute("keyword", keyword);
        model.addAttribute("dobStart", dobStart);
        model.addAttribute("dobEnd", dobEnd);
        model.addAttribute("dobFilter", dobFilter);
        model.addAttribute("genderFilter", genderFilter);
        model.addAttribute("amountStart", amountStart);
        model.addAttribute("amountEnd", amountEnd);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("totalPage", customerList.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalItems", customerList.getTotalElements());
        model.addAttribute("customerList", customerList);
        model.addAttribute("historyTrans", customerService.historyTrans());
        return "/admin/customer";
    }

    @GetMapping("/customer/findCustomerID")
    @ResponseBody
    public Customer findCustomer(@RequestParam Long customerID) throws DataNotFoundException {
        return customerService.findCus(customerID);
    }

    @PostMapping("/customer/save")
    public String saveCustomer(Customer customer, RedirectAttributes ra){
        try {
            customerRepository.save(customer);
            ra.addFlashAttribute("messageSuccess", "Cập nhật dữ liệu thành công!");
        }catch (DatabaseAccessException e){
            ra.addFlashAttribute("messageFail", "Cập nhật dữ liệu thất bại!");
        }
        return "redirect:/admin/customer";
    }

    @GetMapping("/customer/deactivate/{customerID}")
    public String deactivatedCustomer(@PathVariable Long customerID, RedirectAttributes ra){
        try {
            findCustomer(customerID);
            customerService.deactivated(customerID);
            ra.addFlashAttribute("messageSuccess", "Ngừng hoạt động khách hàng thành công!");
        }catch (DataNotFoundException e){
            ra.addFlashAttribute("messageFail", "Ngừng hoạt động khách hàng thất bại!");
        }
        return "redirect:/admin/customer";
    }

    @GetMapping("/customer/activate/{customerID}")
    public String activatedCustomer(@PathVariable Long customerID, RedirectAttributes ra){
        try {
            findCustomer(customerID);
            customerService.activated(customerID);
            ra.addFlashAttribute("messageSuccess", "Bật hoạt động khách hàng thành công!");
        }catch (DataNotFoundException e){
            ra.addFlashAttribute("messageFail", "Bật hoạt động khách hàng thất bại!");
        }
        return "redirect:/admin/customer";
    }

    @GetMapping("/customer/delete/{customerID}")
    public String deletedCustomer(@PathVariable Long customerID, RedirectAttributes ra){
        try {
            findCustomer(customerID);
            customerService.deleted(customerID);
            ra.addFlashAttribute("messageSuccess", "Xoá dữ liệu khách hàng thành công!");
        } catch (DataNotFoundException e){
            ra.addFlashAttribute("messageFail", "Xoá dữ liệu khách hàng thất bại!");
        }
        return "redirect:/admin/customer";
    }
    
}

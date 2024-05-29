package com.nhung.shoptn.controller;

import com.nhung.shoptn.exception.DataNotFoundException;
import com.nhung.shoptn.exception.DatabaseAccessException;
import com.nhung.shoptn.model.Supplier;
import com.nhung.shoptn.model.User;
import com.nhung.shoptn.repository.SupplierRepository;
import com.nhung.shoptn.repository.UserRepository;
import com.nhung.shoptn.service.SupplierService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
@SessionAttributes("loggedInUser")
public class SupplierController {
    @Autowired private UserRepository userRepository;
    @Autowired private SupplierService supplierService;

    @ModelAttribute("loggedInUser")
    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return userRepository.findUserByEmail(user.getEmail());
    }

    @GetMapping("/supplier")
    public String viewSupplierPage(Model model,
                                   @Param("keyword") String keyword,
                                   @Param("groupFilter") Long groupFilter,
                                   @Param("payStatusFilter") Integer payStatusFilter,
                                   @RequestParam(name = "statusFilter", defaultValue = "1") String statusFilter,
                                   @RequestParam(name = "pageNo", defaultValue = "1") int pageNo){
        List<Integer> statusList = new ArrayList<>();
        if (statusFilter != null){
            if (statusFilter.equals("1")){
                statusList.add(1);
            } else if (statusFilter.equals("2")){
                statusList.add(2);
            } else {
                String[] arr1 = statusFilter.split(",");
                for (String s : arr1) {
                    statusList.add(Integer.parseInt(s.trim()));
                }
            }
        } else {statusList = null;}

        if (keyword == null || keyword.trim().isEmpty()){
            keyword = null;
        }
        Page<List<Object[]>> supplierList;
        int totalAmount;
        if (payStatusFilter != null){
            if (payStatusFilter == 1) {
                supplierList = supplierService.filterSupplier2(groupFilter, statusList, keyword, pageNo);
                totalAmount = supplierService.totalAmount2(groupFilter, statusList, keyword);
            }
            else {
                supplierList = supplierService.filterSupplier1(groupFilter, statusList, keyword, pageNo);
                totalAmount = supplierService.totalAmount1(groupFilter, statusList, keyword);
            }
        } else {
            supplierList = supplierService.filterSupplier(groupFilter, statusList, keyword, pageNo);
            totalAmount = supplierService.totalAmount(groupFilter, statusList, keyword);
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("groupFilter", groupFilter);
        model.addAttribute("payStatusFilter", payStatusFilter);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("totalPage", supplierList.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalItems", supplierList.getTotalElements());
        model.addAttribute("supplierList", supplierList);
        model.addAttribute("listGroup", supplierService.listGroup());
        model.addAttribute("historyTrans", supplierService.historyTrans());
        model.addAttribute("debtSupplier", supplierService.debtSupplier());
        model.addAttribute("getSupplierNameList", supplierService.getSupplierNameList());
        return "/admin/supplier";
    }

    @GetMapping("/supplier/findSupplierID")
    @ResponseBody
    public Supplier findSupplier(@RequestParam Long supplierID) throws DataNotFoundException {
        return supplierService.findSupplier(supplierID);
    }

    @PostMapping("/supplier/save/group")
    public String saveSupplierGroup(Supplier supplier, RedirectAttributes ra){
        try {
            supplier.setGroup(true);
            supplierService.save(supplier);
            ra.addFlashAttribute("messageSuccess", "Cập nhật dữ liệu thành công!");
        } catch (DataAccessException e){
            ra.addFlashAttribute("messageFail", "Đã có nhóm NCC này trong hệ thống");
        }
        return "redirect:/admin/supplier";
    }

    @PostMapping("/supplier/save")
    public String saveSupplier(Supplier supplier, RedirectAttributes ra){
        try {
            supplierService.save(supplier);
            ra.addFlashAttribute("messageSuccess", "Cập nhật dữ liệu thành công!");
        } catch (DataAccessException e){
        ra.addFlashAttribute("messageFail", "Cập nhật dữ liệu thất bại!");
    }
        return "redirect:/admin/supplier";
    }

    @GetMapping("/supplier/deactivate/{supplierID}")
    public String deactivatedSupplier(@PathVariable Long supplierID, RedirectAttributes ra){
        try {
            findSupplier(supplierID);
            supplierService.deactivated(supplierID);
            ra.addFlashAttribute("messageSuccess", "Ngừng hoạt động nhà cung cấp thành công!");
        }catch (DataNotFoundException e){
            ra.addFlashAttribute("messageFail", "Ngừng hoạt động nhà cung cấp thất bại!");
        }
        return "redirect:/admin/supplier";
    }

    @GetMapping("/supplier/activate/{supplierID}")
    public String activatedSupplier(@PathVariable Long supplierID, RedirectAttributes ra){

        try {
            findSupplier(supplierID);
            supplierService.activated(supplierID);
            ra.addFlashAttribute("messageSuccess", "Bật hoạt động nhà cung cấp thành công!");
        }catch (DataNotFoundException e){
            ra.addFlashAttribute("messageFail", "Bật hoạt động nhà cung cấp thất bại!");
        }
        return "redirect:/admin/supplier";
    }

    @GetMapping("/supplier/delete/{supplierID}")
    public String deletedSupplier(@PathVariable Long supplierID, RedirectAttributes ra){
        try {
            findSupplier(supplierID);
            supplierService.deleted(supplierID);
            ra.addFlashAttribute("messageSuccess", "Xoá dữ liệu nhà cung cấp thành công!");
        } catch (DataNotFoundException e){
            ra.addFlashAttribute("messageFail", "Xoá dữ liệu nhà cung cấp thất bại!");
        }
        return "redirect:/admin/supplier";
    }


}

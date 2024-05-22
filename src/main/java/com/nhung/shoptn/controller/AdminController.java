package com.nhung.shoptn.controller;

import com.nhung.shoptn.exception.DataNotFoundException;
import com.nhung.shoptn.model.Role;
import com.nhung.shoptn.model.User;
import com.nhung.shoptn.repository.UserRepository;
import com.nhung.shoptn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@SessionAttributes("loggedInUser")
@RequiredArgsConstructor
public class AdminController {
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    @ModelAttribute("loggedInUser")
    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return userRepository.findUserByEmail(user.getEmail());
    }
    @GetMapping
    public String viewAdminHomepage(Model model){
        List<Object[]> listByQuantity = userService.HighestQuantitySold();
        List<Object[]> listByAmount = userService.HighestAmountSold();
        model.addAttribute("quantityTop10", listByQuantity);
        model.addAttribute("amountTop10", listByAmount);
        model.addAttribute("totalQuantityP", userService.totalQuantity());
        model.addAttribute("countP", userService.countProduct());
        model.addAttribute("amountAndCountInvoice", userService.getAmountAndCountInvoice());
        model.addAttribute("amountAndCountPO", userService.getAmountAndCountPO());
        model.addAttribute("getAmountToday", userService.getAmountToday());
        return "/admin/index";
    }

    @GetMapping("/account")
    public String viewAccountPage(Model model,
                                  @Param("keyword") String keyword,
                                  @Param("roleFilter") Role roleFilter,
                                  @RequestParam(name = "statusFilter", defaultValue = "1") String statusFilter,
                                  @RequestParam(name = "pageNo", defaultValue = "1") int pageNo){
        List<String> rolesList = userRepository.getRolesList();
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
        Page<User> userList;
        if (roleFilter == null) {
            userList = userService.filterUser(null, statusList, keyword, pageNo);
        } else {
            userList = userService.filterUser(roleFilter,statusList, keyword, pageNo);
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("roleFilter", roleFilter);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("rolesList", rolesList);
        model.addAttribute("totalPage", userList.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("accountList", userList);
        model.addAttribute("totalItems", userList.getTotalElements());
        return "admin/account";
    }

    @PostMapping("/account/save")
    public String saveAccount(User user, RedirectAttributes ra){
        try {
            if (user.getPassword().isEmpty()){
                User findUser = findUser(user.getId());
                User updateUser = new User();
                updateUser.setId(user.getId());
                updateUser.setName(user.getName());
                updateUser.setEmail(user.getEmail());
                updateUser.setPassword(new BCryptPasswordEncoder().encode(findUser.getPassword()));
                updateUser.setPhone(user.getPhone());
                updateUser.setAddress(user.getAddress());
                updateUser.setRole(user.getRole());
                updateUser.setIsEnable(user.getIsEnable());
                userRepository.save(updateUser);
            }
            else {
                userRepository.save(new User(user.getName(),
                        user.getEmail(),
                        new BCryptPasswordEncoder().encode(user.getPassword()),
                        user.getPhone(),
                        user.getAddress(),
                        user.getRole(),
                        user.getIsEnable()));
            }
            ra.addFlashAttribute("messageSuccess", "Cập nhật dữ liệu thành công!");
        }catch (DataNotFoundException e){
            ra.addFlashAttribute("messageFail", "Cập nhật dữ liệu thất bại!");
        }
        return "redirect:/admin/account";
    };

    @GetMapping("/account/findUserID")
    @ResponseBody
    public User findUser(@RequestParam Long userID) throws DataNotFoundException {
        return userService.findUser(userID);
    }

    @GetMapping("account/deactivate/{userID}")
    public String deactivatedAccount(@PathVariable Long userID, RedirectAttributes ra) {
        try {
            findUser(userID);
            userRepository.deactivated(userID);
            ra.addFlashAttribute("messageSuccess", "Vô hiệu hoá người dùng thành công!");
        } catch (DataNotFoundException e){
            ra.addFlashAttribute("messageFail", "Vô hiệu hoá người dùng thất bại!");
        }
        return "redirect:/admin/account";
    }

    @GetMapping("account/activate/{userID}")
    public String activatedAccount(@PathVariable Long userID, RedirectAttributes ra){
        try {
            findUser(userID);
            userRepository.activated(userID);
            ra.addFlashAttribute("messageSuccess", "Bật hoạt động người dùng thành công!");
        } catch (DataNotFoundException e){
            ra.addFlashAttribute("messageFail", "Bật hoạt động người dùng thất bại!");
        }
        return "redirect:/admin/account";
    }

    @GetMapping("account/delete/{userID}")
    public String deletedAccount(@PathVariable Long userID, RedirectAttributes ra){
        try {
            findUser(userID);
            userRepository.deleted(userID);
            ra.addFlashAttribute("messageSuccess", "Xoá dữ liệu người dùng thành công!");
        } catch (DataNotFoundException e){
            ra.addFlashAttribute("messageFail", "Xoá dữ liệu người dùng thất bại!");
        }
        return "redirect:/admin/account";
    }

    @PostMapping("account/changePw/{userID}")
    public String changePass(@PathVariable Long userID,
                             @RequestParam("changePw") String pass,
                             RedirectAttributes ra){
        try {
            User user = findUser(userID);
            userRepository.changePw(userID, new BCryptPasswordEncoder().encode(pass));
            ra.addFlashAttribute("messageSuccess", "Đổi mật khẩu thành công!");
        } catch (DataNotFoundException e){
            ra.addFlashAttribute("messageFail", "Đổi mật khẩu thất bại!");
        }
        return "redirect:/admin/account";
    }
}

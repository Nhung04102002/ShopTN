package com.nhung.shoptn.controller;
import com.nhung.shoptn.model.Category;
import com.nhung.shoptn.model.User;
import com.nhung.shoptn.repository.CategoryRepository;
import com.nhung.shoptn.repository.UserRepository;
import com.nhung.shoptn.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@SessionAttributes("loggedInUser")

public class CategoryController {
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryService categoryService;

    @ModelAttribute("loggedInUser")
    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return userRepository.findUserByEmail(user.getEmail());
    }

    @PostMapping("/category/save")
    public String saveCategory(Category category, RedirectAttributes ra){
        try {
            categoryService.save(category);
            ra.addFlashAttribute("messageSuccess", "Cập nhật dữ liệu thành công!");
        } catch (DataAccessException e){
            ra.addFlashAttribute("messageFail","Đã có nhóm hàng này trong hệ thống");
        }
        return "redirect:/admin/product";
    }

    @GetMapping("/category/delete")
    public String deleteCategory(Long categoryID, RedirectAttributes ra){
        try {
            categoryService.deleteCategory(categoryID);
            ra.addFlashAttribute("messageSuccess", "Xoá nhóm hàng thành công!");
        } catch (DataAccessException e){
            ra.addFlashAttribute("messageFail","Không thể xoá vì có hàng trong nhóm");
        }
        return "redirect:/admin/product";
    }

    @GetMapping("/category/findCategoryID")
    @ResponseBody
    public Category findCategory(@RequestParam Long categoryID){
        return categoryService.findCategory(categoryID);
    }
}


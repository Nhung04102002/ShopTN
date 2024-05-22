package com.nhung.shoptn.controller;

import com.nhung.shoptn.exception.DataNotFoundException;
import com.nhung.shoptn.exception.DatabaseAccessException;
import com.nhung.shoptn.model.Category;
import com.nhung.shoptn.model.Product;
import com.nhung.shoptn.model.User;
import com.nhung.shoptn.repository.ProductRepository;
import com.nhung.shoptn.repository.UserRepository;
import com.nhung.shoptn.service.CategoryService;
import com.nhung.shoptn.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class ProductController {
    @Autowired private UserRepository userRepository;
    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;
    @Autowired private ProductRepository productRepository;

    @ModelAttribute("loggedInUser")
    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return userRepository.findUserByEmail(user.getEmail());
    }

    @GetMapping("/product")
    public String viewProductPage(Model model, @Param("keyword") String keyword,
                                  @Param("categoryFilter") Long categoryFilter,
                                  @Param("inventoryFilter") Integer inventoryFilter,
                                  @Param("locationFilter") String locationFilter,
                                  @Param("statusFilter") Integer statusFilter,
                                  @Param("supplierFilter") String supplierFilter,
                                  @RequestParam(name = "pageNo", defaultValue = "1") int pageNo){
        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("newCategory", new Category());
        model.addAttribute("unitList", productRepository.unitList());
        model.addAttribute("brandList", productRepository.brandList());
        model.addAttribute("supplierList", productRepository.getSupplierList());
        model.addAttribute("locationList", productRepository.locationList());
        int min, max;
        List<Long> supplierList = new ArrayList<>();
        if (inventoryFilter == null){inventoryFilter = 1;}
        if (statusFilter == null){statusFilter = 1;};
        if (inventoryFilter == 1){
            min = 1;
            max = 9999999;
        } else if (inventoryFilter == 0) {
            min = 0;
            max = 0;
        } else if (inventoryFilter == 3) {
            min = 0;
            max = 9999999;
        } else {
            min = 0;
            max = -1;
        }

        if (supplierFilter != null) {
            String[] arr = supplierFilter.split(",");
            for (String s : arr) {
                supplierList.add(Long.parseLong(s.trim()));
            }
        } else {supplierList = null;}

        if (keyword == null || keyword.trim().isEmpty()){
            keyword = null;
        }

        Page<Product> productList;
        if (statusFilter != 3) {
            productList = productService.filterProduct(categoryFilter, statusFilter, supplierList, locationFilter, min, max, keyword, pageNo);
        } else {
            productList = productService.filterProduct1(categoryFilter, supplierList, locationFilter, min, max, keyword, pageNo);
        }
        int totalQuantity = productRepository.totalQuantity();

        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryFilter", categoryFilter);
        model.addAttribute("inventoryFilter", inventoryFilter);
        model.addAttribute("locationFilter", locationFilter);
        model.addAttribute("supplierFilter", supplierFilter);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("productList", productList);
        model.addAttribute("totalPage", productList.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalItems", productList.getTotalElements());
        model.addAttribute("getSupplierOfProduct", productRepository.getSupplierOfProduct());
        return "/admin/product";
    }

    @GetMapping("/product/findProductID")
    @ResponseBody
    public Product findProduct(@RequestParam Long productID)  throws DataNotFoundException {
        return productService.findProduct(productID);
    }

    @PostMapping("/product/save")
    public String saveProduct(Product product,
                              @RequestParam("price1") String price,
                              @RequestParam("cogs1") String cogs,
                              @RequestParam("fileImage")MultipartFile multipartFile,
                              RedirectAttributes ra) throws IOException {
        try {
            if(product.getStatus() == 0){
                product.setStatus(1);
            }

            if (price != null && !price.isEmpty()){
                product.setPrice(Integer.parseInt(price.replace(".", "")));
            } else {
                product.setPrice(0);
            }

            if (cogs != null && !cogs.isEmpty()){
                product.setCogs(Integer.parseInt(cogs.replace(".", "")));
            } else {
                product.setCogs(0);
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
        } catch (DatabaseAccessException e){
            ra.addFlashAttribute("messageFail", "Cập nhật dữ liệu thất bại!");
        }

    return "redirect:/admin/product";
}

    private void uploadImage(Product product, @RequestParam("fileImage") MultipartFile multipartFile, String fileName) throws IOException {
        uploadImage1(product, multipartFile, fileName);
    }

    static void uploadImage1(Product product, @RequestParam("fileImage") MultipartFile multipartFile, String fileName) throws IOException {
        String uploadDir = "./src/main/resources/static/images/" + product.getProductID();
        Path uploadPath = Paths.get(uploadDir);

        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        if (!Files.exists(uploadPath.resolve(fileName))) {
            try(InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e){
                throw new IOException("Could not save uploaded file: " + fileName);
            }
        }
    }

    @GetMapping("/product/deactivate/{productID}")
    public String deactivatedProduct(@PathVariable Long productID, RedirectAttributes ra){
        try {
            productRepository.deactivated(productID);
            ra.addFlashAttribute("messageSuccess", "Bật trạng thái hàng ngừng bán thành công!");
        } catch (DatabaseAccessException e){
            ra.addFlashAttribute("messageFail", "Bật trạng thái hàng ngừng bán thất bại!");
        }
        return "redirect:/admin/product";
    }

    @GetMapping("/product/activate/{productID}")
    public String activatedProduct(@PathVariable Long productID, RedirectAttributes ra){
        try {
            productRepository.activated(productID);
            ra.addFlashAttribute("messageSuccess", "Bật trạng thái hàng đang bán thành công!");
        } catch (DatabaseAccessException e){
            ra.addFlashAttribute("messageFail", "Bật trạng thái hàng đang bán thất bại!");
        }

        return "redirect:/admin/product";
    }

    @GetMapping("/product/delete/{productID}")
    public String deletedProduct(@PathVariable Long productID, RedirectAttributes ra){
        try {
            findProduct(productID);
            productRepository.deleted(productID);
            ra.addFlashAttribute("messageSuccess", "Xoá dữ liệu hàng hoá thành công!");
        }catch (DataNotFoundException e){
            ra.addFlashAttribute("messageFail", "Xoá dữ liệu hàng hoá thất bại!");
        }
        return "redirect:/admin/product";
    }

}

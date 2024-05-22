package com.nhung.shoptn.service;

import com.nhung.shoptn.exception.DataNotFoundException;
import com.nhung.shoptn.model.Product;
import com.nhung.shoptn.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product findProduct(Long productID) throws DataNotFoundException {
        Product product = productRepository.findByProductID(productID);
        if (product != null) {
            return product;
        }
        throw new DataNotFoundException("Không tìm thấy hàng hoá nào có ID là "+ productID);
    }

    public Page<Product> filterProduct(Long categoryID, int status, List<Long> supplier, String location, int min, int max, String keyword, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return productRepository.filterProduct(categoryID, status, supplier, location, min, max, keyword, pageable);
    }

    public Page<Product> filterProduct1(Long categoryID, List<Long> supplier, String location, int min, int max, String keyword, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return productRepository.filterProduct1(categoryID, supplier, location, min, max, keyword, pageable);
    }
}

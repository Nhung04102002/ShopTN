package com.nhung.shoptn.service;

import com.nhung.shoptn.model.Category;
import com.nhung.shoptn.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    @Autowired private CategoryRepository categoryRepository;

    public List<Category> findAll(){ return categoryRepository.findAll(); }

    public void save(Category category) { categoryRepository.save(category); }
    public void deleteCategory(Long categoryID){ categoryRepository.deleteById(categoryID); }

    public Category findCategory(Long categoryID){
        return categoryRepository.findOneCategory(categoryID);
    }
}



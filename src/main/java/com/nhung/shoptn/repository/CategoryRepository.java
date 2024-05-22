package com.nhung.shoptn.repository;

import com.nhung.shoptn.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c from Category c")
    List<Category> findAll();

    @Query("SELECT c from Category c where c.categoryID = :categoryID")
    Category findOneCategory(@Param("categoryID") Long categoryID);
}


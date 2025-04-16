package com.ecommerce.service;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.dto.CreateCategoryRequest;
import com.ecommerce.dto.UpdateCategoryRequest;

public interface CategoryService {
    CategoryDto createCategory(CreateCategoryRequest request);
    CategoryDto updateCategory(Long id, UpdateCategoryRequest request);
    void deleteCategory(Long id);
}
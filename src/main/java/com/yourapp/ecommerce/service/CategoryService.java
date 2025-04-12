package com.yourapp.ecommerce.service;

import com.yourapp.ecommerce.dto.CategoryDto;
import com.yourapp.ecommerce.dto.CreateCategoryRequest;
import com.yourapp.ecommerce.entity.Category;
import com.yourapp.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryDto createCategory(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        Category saved = categoryRepository.save(category);
        return new CategoryDto(saved.getId(), saved.getName());
    }
}
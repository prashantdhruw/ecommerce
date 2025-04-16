package com.ecommerce.service;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.dto.CreateCategoryRequest;
import com.ecommerce.dto.UpdateCategoryRequest;
import com.ecommerce.entity.Category;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDto createCategory(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        Category saved = categoryRepository.save(category);
        return new CategoryDto(saved.getId(), saved.getName());
    }

    @Override
    public CategoryDto updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setName(request.getName());
        Category updatedCategory = categoryRepository.save(category);
        return new CategoryDto(updatedCategory.getId(), updatedCategory.getName());
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }
}
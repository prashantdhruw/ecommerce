package com.ecommerce.service;

import com.ecommerce.dto.CreateProductRequest;
import com.ecommerce.dto.ProductDto;
import com.ecommerce.dto.UpdateProductRequest;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.ConcurrentModificationException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductDto createProduct(CreateProductRequest request) {
        log.info("Creating product with name: {}", request.getName());
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        Product saved = productRepository.save(product);
        log.info("Product created successfully with ID: {}", saved.getId());
        return toDto(saved);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findByIdFetchingCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toDto(product);
    }

    public ProductDto updateProduct(Long id, UpdateProductRequest request) {
        log.info("Initiating update for product ID: {}", id);
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStockQuantity(request.getStockQuantity());
            product.setCategory(category);

            Product updated = productRepository.save(product);
            log.info("Product updated successfully with ID: {}", updated.getId());
            return toDto(updated);
        } catch (OptimisticLockingFailureException ex) {
            log.warn("Concurrent modification detected while updating product ID: {}", id, ex);
            throw new ConcurrentModificationException("Failed to update product due to concurrent modification", ex);
        } catch (Exception ex) {
            log.error("Unexpected error while updating product ID: {}", id, ex);
            throw new RuntimeException("Failed to update product due to an unexpected error", ex);
        }
    }

    public void deleteProduct(Long id) {
        log.info("Initiating deletion of product ID: {}", id);
        if (!productRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent product ID: {}", id);
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted successfully with ID: {}", id);
    }

    public Page<ProductDto> getAllProducts(Pageable pageable, Optional<String> categoryName, Optional<String> search) {
        Specification<Product> spec = Specification.where(null);

        if (categoryName.isPresent()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("category").get("name"), categoryName.get()));
        }

        if (search.isPresent()) {
            String pattern = "%" + search.get().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), pattern));
        }

        return productRepository.findAll(spec, pageable).map(this::toDto);
    }

    private ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}
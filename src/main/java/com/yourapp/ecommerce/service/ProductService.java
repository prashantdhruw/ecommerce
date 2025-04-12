package com.yourapp.ecommerce.service;

import com.yourapp.ecommerce.dto.CreateProductRequest;
import com.yourapp.ecommerce.dto.ProductDto;
import com.yourapp.ecommerce.dto.UpdateProductRequest;
import com.yourapp.ecommerce.entity.Category;
import com.yourapp.ecommerce.entity.Product;
import com.yourapp.ecommerce.repository.CategoryRepository;
import com.yourapp.ecommerce.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        Product saved = productRepository.save(product);
        log.info("Product created with ID: {}", saved.getId());
        return toDto(saved);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findByIdFetchingCategory(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return toDto(product);
    }

    public ProductDto updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        Product updated = productRepository.save(product);
        log.info("Product updated with ID: {}", updated.getId());
        return toDto(updated);
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        if (!productRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent product with ID: {}", id);
            throw new EntityNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
        log.info("Product deleted with ID: {}", id);
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
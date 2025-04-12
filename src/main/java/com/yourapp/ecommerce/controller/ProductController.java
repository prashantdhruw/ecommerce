package com.yourapp.ecommerce.controller;

import com.yourapp.ecommerce.dto.ProductDto;
import com.yourapp.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            Pageable pageable,
            @RequestParam Optional<String> category,
            @RequestParam Optional<String> search) {
        return ResponseEntity.ok(productService.getAllProducts(pageable, category, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}
package com.ECommerceApp.Controller.Product;

import com.ECommerceApp.DTO.Product.ProductCreationRequest;
import com.ECommerceApp.DTO.Product.ProductSearchResponse;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.ServiceInterface.Product.IProductService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController { // admin, seller

    @Autowired
    private IProductService productService;

    //  SELLER
    @PreAuthorize("hasPermission('PRODUCT', 'INSERT')")
    @PostMapping("/insertProduct")
    public ResponseEntity<?> insertProduct(@Valid @RequestBody ProductCreationRequest product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    //  SELLER
    @PreAuthorize("hasPermission('PRODUCT', 'INSERT')")
    @PostMapping("/insertProducts")
    public ResponseEntity<?> insertProducts(@Valid @RequestBody List<@Valid ProductCreationRequest> product) {
        return ResponseEntity.ok(productService.createProductList(product));
    }

    //  ALL ROLES
    @PreAuthorize("hasPermission('PRODUCT', 'READ')")
    @GetMapping("/getProduct/{id}")
    public ProductSearchResponse getProduct(@PathVariable String id) {
        Product product = productService.getProductById(id);
        ProductSearchResponse dto = new ProductSearchResponse();
        BeanUtils.copyProperties(product, dto);
        return dto;
    }

    //  ALL ROLES
    @PreAuthorize("hasPermission('PRODUCT', 'READ')")
    @GetMapping("/getAllProducts")
    public List<Product> get() {
        return productService.getAllProducts();
    }

    //  SELLER
    @PreAuthorize("hasPermission('PRODUCT', 'UPDATE')")
    @PutMapping("/updateProduct")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductCreationRequest product) {
        return ResponseEntity.ok(productService.updateProduct(product));
    }

    //  ADMIN
    @PreAuthorize("hasPermission('PRODUCT', 'READ')")
    @GetMapping("/getProductCountBySeller/{sellerId}")
    public long getProductCountBySeller(@PathVariable String sellerId) {
        return productService.getTotalCountOfProductBySeller(sellerId);
    }

    //  SELLER
    @PreAuthorize("hasPermission('PRODUCT', 'DELETE')")
    @DeleteMapping("/deleteProduct/{productId}")
    public String deleteProduct(@PathVariable String productId) {
        return productService.deleteProduct(productId);
    }


}

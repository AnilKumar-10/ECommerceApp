package com.ECommerceApp.Controller.Product;

import com.ECommerceApp.DTO.Product.ProductCreationRequest;
import com.ECommerceApp.DTO.Product.ProductSearchResponse;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ProductController { // admin, seller

    @Autowired
    private ProductService productService;

    @PostMapping("/insertProduct")
    public ResponseEntity<?> insertProduct(@Valid @RequestBody ProductCreationRequest product, BindingResult result){
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @PostMapping("/insertProducts")
    public ResponseEntity<?>  insertProducts(@Valid @RequestBody List<@Valid ProductCreationRequest> product){
        return ResponseEntity.ok(productService.createProductList(product));
    }

    @GetMapping("/getProduct/{id}")
    public ProductSearchResponse getProduct(@PathVariable String id){
        Product product = productService.getProductById(id);
        ProductSearchResponse productSearchResponseDto = new ProductSearchResponse();
        BeanUtils.copyProperties(product,productSearchResponseDto);
        return productSearchResponseDto;
    }

    @GetMapping("/getAllProducts")
    public List<Product> get(){
        return productService.getAllProducts();
    }


    @PutMapping("/updateProduct")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductCreationRequest product){
        return ResponseEntity.ok(productService.updateProduct(product));
    }


    @GetMapping("/getProductCountBySeller/{sellerId}")
    public long getProductCountBySeller(@PathVariable String sellerId){
        return productService.getTotalCountOfProductBySeller(sellerId);
    }


    @GetMapping("/deleteProduct/{productId}")
    public String deleteProduct(@PathVariable String productId){
        return productService.deleteProduct(productId);
    }


}
